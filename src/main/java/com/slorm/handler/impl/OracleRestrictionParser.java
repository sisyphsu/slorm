package com.slorm.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.slorm.core.Restriction;
import com.slorm.core.Util;
import com.slorm.expression.*;

/**
 * Restriction的Oracle数据库解析器。
 * 此类为最终类且线程安全的工具类
 * 
 * @author sulin 2012-4-5
 * @version 1.0
 */
public final class OracleRestrictionParser {

	/**
	 * 解析Restriction
	 * @param r
	 * @return
	 */
	public static ParsedRestriction parseRestriction(Restriction<?> r){
		ParsedRestriction result = new ParsedRestriction();
		StringBuffer sb = new StringBuffer();
		List<Expression> expressions = Util.getExpressions(r);
		List<Order> order = new ArrayList<Order>();
		for(Expression expression : expressions){
			if(expression instanceof Order){
				order.add((Order)expression);
			}else{
				if(sb.length()!=0)
					sb.append(" AND ");
				else
					sb.append(" WHERE ");
				parseExpression(expression, sb, result); // 解析Expression
			}
		}
		// Order处理
		for(int i=0, size=order.size(); i<size; i++){
			if(i==0)
				sb.append(" ORDER BY ");
			else
				sb.append(',');
			Order temp = order.get(i);
			sb.append(temp.getProp().getColumn());
			if(temp.isAsc())
				sb.append(" ASC");
			else
				sb.append(" DESC");
		}
		result.setSql(sb.toString());
		
		return result;
	}
	
	/**
	 * 解析Expression
	 * @param expression
	 * @param sb
	 * @param result
	 */
	public static void parseExpression(Expression expression, StringBuffer sb, ParsedRestriction result){
		// Between处理
		if(expression instanceof Between){
			Between temp = (Between)expression;
			sb.append('(');
			sb.append(temp.getProp().getColumn()).append(" BETWEEN ? AND ?");
			sb.append(')');
			result.addColumn(temp.getProp(), temp.getFrom());
			result.addColumn(temp.getProp(), temp.getTo());
		}
		// Compare处理
		if(expression instanceof Compare){
			Compare temp = (Compare)expression;
			sb.append('(');
			sb.append(temp.getProp().getColumn());
			switch(temp.getType()){
				case Compare.EQUAL : 
					sb.append("=?");
					break;
				case Compare.LARGE :
					sb.append(">?");
					break;
				case Compare.LARGE_EQUAL :
					sb.append(">=?");
					break;
				case Compare.NOT_EQUAL :
					sb.append("<>?");
					break;
				case Compare.SMAILL :
					sb.append("<?");
					break;
				case Compare.SMAILL_EQUAL :
					sb.append("<=?");
					break;
			}
			sb.append(')');
			result.addColumn(temp.getProp(), temp.getParam());
		}
		// In处理
		if(expression instanceof In){
			In temp = (In)expression;
			sb.append('(');
			sb.append(temp.getProp().getColumn()).append(" IN(");
			Object[] params = temp.getParams();
			for(int i=0, size=params.length; i<size; i++){
				if(i != 0)
					sb.append(',');
				sb.append('?');
				result.addColumn(temp.getProp(), params[i]);
			}
			sb.append("))");
		}
		// IsNULL 处理
		if(expression instanceof IsNULL){
			IsNULL temp = (IsNULL)expression;
			sb.append('(');
			sb.append(temp.getProp().getColumn());
			if(temp.isNull())
				sb.append(" IS NULL");
			else
				sb.append(" IS NOT NULL");
			sb.append(')');
		}
		// Like 处理
		if(expression instanceof Like){
			Like temp = (Like)expression;
			sb.append('(');
			sb.append(temp.getProp().getColumn()).append(" LIKE ?");
			sb.append(')');
			result.addColumn(temp.getProp(), temp.getParam());
		}
		// Limit 处理
		if(expression instanceof Limit){
			Limit temp = (Limit)expression;
			sb.append('(');
			sb.append("rownum>=?").append(" AND rownum<?");
			sb.append(')');
			result.addColumn(null, temp.getFrom()); // null表示行！！！
			result.addColumn(null, temp.getPageSize()); // null表示行！！！
		}
		// AND 处理
		if(expression instanceof And){
			And temp = (And)expression;
			Expression[] es = temp.getExpressions();
			sb.append('(');
			for(int i=0, size=es.length; i<size; i++){
				if(i!=0)
					sb.append(" AND ");
				parseExpression(es[i], sb, result);
			}
			sb.append(')');
		}
		// Or 处理
		if(expression instanceof Or){
			Or temp = (Or)expression;
			Expression[] es = temp.getExpressions();
			sb.append('(');
			for(int i=0, size=es.length; i<size; i++){
				if(i!=0)
					sb.append(" OR ");
				parseExpression(es[i], sb, result);
			}
			sb.append(')');
		}
	}
	
}