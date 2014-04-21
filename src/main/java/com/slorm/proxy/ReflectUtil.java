package com.slorm.proxy;

import com.slorm.core.*;
import com.slorm.handler.BaseHandler;
import javassist.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射操作工具类<br/>
 * 此类为最终类且线程安全！
 * @author sulin
 * @date 2012-4-11 上午08:34:51
 */
public final class ReflectUtil {
	
	/**
	 * Model封装类容器
	 */
	public static ConcurrentHashMap<Class<?>, ModelWrapper> container = new ConcurrentHashMap<Class<?>, ModelWrapper>();
	
	/**
	 * 从指定对象中获取指定名称的字段
	 * @param target
	 * @param propertyName
	 * @return
	 */
	public static Object get(Object target, String propertyName){
		return getWrapper(target.getClass()).get(target, propertyName);
	}
	
	/**
	 * 将指定字段置入指定对象中
	 * @param target
	 * @param propertyName
	 * @param propertyValue
	 */
	public static void set(Object target, String propertyName, Object propertyValue){
		getWrapper(target.getClass()).set(target, propertyName, propertyValue);
	}
	
	/**
	 * 构造指定Model的一个实例。
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> T newInstance(Class<T> clazz){
		return (T)getWrapper(clazz).newInstance();
	}
	
	public static final String PROXYSUFFIX = "$OTM$Proxy";
	
	public static final String WRAPPERSUFFIX = "$ReflectWrapper";
	
	// 获取Model封装器
	private static ModelWrapper getWrapper(Class<?> clazz){
		while(clazz.getName().endsWith(PROXYSUFFIX))
			clazz = clazz.getSuperclass();
		ModelWrapper mw = container.get(clazz);
		if(mw != null) {
			return mw;
		} else {
			try {
				mw = (ModelWrapper) Class.forName(clazz.getName() + WRAPPERSUFFIX).newInstance();
				container.putIfAbsent(clazz, mw);
				return mw;
			} catch (Exception e) {
				// 正常异常
			}
		}
		
		// 构造此类的封装器
		ClassToTable ctt = MapContainer.getCCT(clazz);
		Assert.isNotNull(ctt, "Cann't get ClassToTable instance");
		CtClass wrapper = null;
		CtClass proxy = null;
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new ClassClassPath(clazz));
			wrapper = pool.makeClass(clazz.getName() + WRAPPERSUFFIX, pool.get(ModelWrapper.class.getName()));
			if(ctt.getQuotes()!=null && !ctt.getQuotes().isEmpty())
				proxy = pool.makeClass(clazz.getName()+PROXYSUFFIX, pool.get(clazz.getName()));
		} catch (Exception e) {
			throw new RuntimeException("Cann't create wrapper class : " + clazz.getName() + WRAPPERSUFFIX, e);
		}
		try {
			// 设计proxy
			if(proxy != null){
				List<Reference> refs = ctt.getQuotes();
				for(Reference ref : refs){
					StringBuilder getSrc = new StringBuilder();
					getSrc.append("public "+ref.getTargetType().getName()+" get"+ref.getName().substring(0,1).toUpperCase()+ref.getName().substring(1)+"(){ \n");
					getSrc.append("if(super.get"+ref.getName().substring(0,1).toUpperCase()+ref.getName().substring(1)+"()==null)\n");
					getSrc.append(BaseHandler.class.getName() + ".loadProxy(\""+ref.getName()+"\", this);");
					getSrc.append("return super.get"+ref.getName().substring(0,1).toUpperCase()+ref.getName().substring(1)+"();");
					getSrc.append("} \n");
					proxy.addMethod(CtNewMethod.make(getSrc.toString(), proxy));
				}
				proxy.toClass();
			}
			
			// get方法
			StringBuilder getSrc = new StringBuilder();
			getSrc.append("public Object get(Object target, String propertyName){ \n");
			getSrc.append("if(propertyName==null) \n return null; \n");
			for(Property p : ctt.getProps()){
				getSrc.append("if(propertyName.equals(\"").append(p.getName()).append("\")) \n");
				getSrc.append("return ((").append(clazz.getName()).append(")target).get");
				getSrc.append(p.getName().substring(0, 1).toUpperCase()).append(p.getName().substring(1)).append("(); \n");
			}
			if(ctt.getQuotes() != null && !ctt.getQuotes().isEmpty()){
				for(Reference ref : ctt.getQuotes()){
					getSrc.append("if(propertyName.equals(\"").append(ref.getName()).append("\")) \n");
					getSrc.append("return ((").append(clazz.getName()).append(")target).get");
					getSrc.append(ref.getName().substring(0, 1).toUpperCase()).append(ref.getName().substring(1)).append("(); \n");
				}
			}
			getSrc.append("return null; \n");
			getSrc.append("} \n");
			wrapper.addMethod(CtNewMethod.make(getSrc.toString(), wrapper));
			
			// set方法
			StringBuilder setSrc = new StringBuilder();
			setSrc.append("public void set(Object target, String propertyName, Object propertyValue){ \n");
			setSrc.append("if(propertyName==null) \n return; \n");
			for(Property p : ctt.getProps()){
				setSrc.append("if(propertyName.equals(\"").append(p.getName()).append("\")) \n");
				setSrc.append("((").append(clazz.getName()).append(")target).set");
				setSrc.append(p.getName().substring(0, 1).toUpperCase()).append(p.getName().substring(1));
				setSrc.append("((").append(p.getType().getName()).append(")propertyValue); \n");
			}
			if(ctt.getQuotes() != null && !ctt.getQuotes().isEmpty()){
				for(Reference ref : ctt.getQuotes()){
					setSrc.append("if(propertyName.equals(\"").append(ref.getName()).append("\")) \n");
					setSrc.append("((").append(clazz.getName()).append(")target).set");
					setSrc.append(ref.getName().substring(0, 1).toUpperCase()).append(ref.getName().substring(1));
					setSrc.append("((").append(ref.getTargetType().getName()).append(")propertyValue); \n");
				}
			}
			setSrc.append("} \n");
			wrapper.addMethod(CtNewMethod.make(setSrc.toString(), wrapper));

			// newInstance方法
			StringBuilder niSrc = new StringBuilder();
			if(proxy != null){
				niSrc.append("public Object newInstance(){ \n");
				niSrc.append("return new ").append(proxy.getName()).append("(); \n");
				niSrc.append("} \n");
			}else{
				niSrc.append("public Object newInstance(){ \n");
				niSrc.append("return new ").append(clazz.getName()).append("(); \n");
				niSrc.append("} \n");
			}
			
			wrapper.addMethod(CtNewMethod.make(niSrc.toString(), wrapper));
		} catch (CannotCompileException e) {
			// e.printStackTrace(); // ignore
		}
		
		try {
			mw = (ModelWrapper) wrapper.toClass().newInstance();;
			container.putIfAbsent(clazz, mw);
			return container.get(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}