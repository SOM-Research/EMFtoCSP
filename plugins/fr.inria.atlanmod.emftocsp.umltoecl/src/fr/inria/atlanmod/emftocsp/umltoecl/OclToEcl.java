/*******************************************************************************
 * Copyright (c) 2011 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.umltoecl;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.types.SetType;
import org.eclipse.ocl.uml.OCL;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.AbstractVisitor;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.PredefinedType;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.SendSignalAction;
import org.eclipse.uml2.uml.State;

import fr.inria.atlanmod.emftocsp.ILogger;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class OclToEcl extends AbstractVisitor<String, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint> {
  
  Environment<?, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint, ?, ?> env = null;
  StringBuilder oclTranslation = new StringBuilder();
  String constraintName;
  String firstPredicate;  
  int counter;
  Stack<String> varStack = null;
  private final UMLReflection<?, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint> uml;
  private static String XML_NULL_PLACEHOLDER = "NONE"; //$NON-NLS-1$  
  ILogger logger;

  public static OclToEcl getInstance(ILogger logger) {
    Environment<Package, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint, Class, EObject> auxEnv = OCL.newInstance().getEnvironment();
    return new OclToEcl(auxEnv, logger);
  }

  public static OclToEcl getInstance(
      Environment<?, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint, ?, ?> env, ILogger logger) {
    return new OclToEcl(env, logger);
  }  

  protected OclToEcl(Environment<?, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint, ?, ?> env, ILogger logger) {
    this.env = env;
    uml = (env == null) ? null : env.getUMLReflection();
    this.logger = logger;
  }

  @Override
  public String visitExpressionInOCL(ExpressionInOCL<Classifier, Parameter> expression) {
    constraintName = ((Constraint)expression.eContainer()).getName();
    oclTranslation = new StringBuilder();
    varStack = new Stack<String>();
    firstPredicate = "";
    counter = 0;
    return super.visitExpressionInOCL(expression);
  }

  @Override
  public String visitIntegerLiteralExp(IntegerLiteralExp<Classifier> literalExp) {
    ++counter;
    String predName = "nConstant" + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(_, _, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("Result=");
    oclTranslation.append(literalExp.getIntegerSymbol());
    oclTranslation.append(".\n");
    return predName;
  }

  @Override
  public String visitRealLiteralExp(RealLiteralExp<Classifier> literalExp) {
    ++counter;
    String predName = "nConstant" + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(_, _, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("Result=");
    oclTranslation.append(literalExp.getRealSymbol());
    oclTranslation.append(".\n");
    return predName;
  }

  @Override
  public String visitStringLiteralExp(StringLiteralExp<Classifier> literalExp) {
    ++counter;
    String predName = "nConstant" + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(_, _, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("Result=");
    oclTranslation.append(literalExp.getStringSymbol());
    oclTranslation.append(".\n");
    return predName;
  }

  @Override
  public String visitBooleanLiteralExp(BooleanLiteralExp<Classifier> literalExp) {
    ++counter;
    String predName = "nConstant" + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(_, _, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("Result=");
    oclTranslation.append(literalExp.getBooleanSymbol() ? "1" : "0");
    oclTranslation.append(".\n");
    return predName;
  }

  @Override
  public String visitTypeExp(TypeExp<Classifier> t) {
    return getName(t.getReferredType());
  }  

  @Override
  protected String handleCollectionItem(CollectionItem<Classifier> item, String itemResult) {
    return itemResult;
  }

  @Override
  protected String handleCollectionLiteralExp(CollectionLiteralExp<Classifier> literalExp, List<String> partResults) {
    if (literalExp.getType() instanceof SetType) {
      String opType = "bag";
      String opCSPName = "asSet";
      ++counter;
      String predName = "n" + opCSPName + counter + constraintName;
      oclTranslation.append(predName);
      oclTranslation.append("(Instances, Vars, Result):-");    
      oclTranslation.append("\n\t");
      oclTranslation.append(partResults.get(0));
      oclTranslation.append("(Instances, Vars, Value1),");
      oclTranslation.append("\n\t");
      oclTranslation.append("ocl_");
      oclTranslation.append(opType.toLowerCase());
      oclTranslation.append("_");
      oclTranslation.append(opCSPName);
      oclTranslation.append("(Value1, Result).\n");
      return predName;
    }
    return "";
  }  

  @Override
  public String visitVariableExp(VariableExp<Classifier, Parameter> v) {
    ++counter;
    String predName = "nVariable" + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(_, Vars, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_variable(Vars,");
    int index = varStack.search(v.getName());
    oclTranslation.append(index);
    oclTranslation.append(",Result).\n");
    return predName;
  }

  @Override
  protected String handleVariable(Variable<Classifier, Parameter> variable, String initResult) {
    return "";
  }

  @Override
  protected String handlePropertyCallExp(PropertyCallExp<Classifier, Property> callExp, String sourceResult, List<String> qualifierResults) {    
    String predName = "";
    Property refProp = callExp.getReferredProperty();    
    String propertyName = getName(refProp);
    ++counter;
    if (refProp.getAssociation() == null) {
      predName = "nAttribute" + counter + constraintName;
      oclTranslation.append(predName);
      oclTranslation.append("(Instances, Vars, Result):-");
      oclTranslation.append("\n\t");
      oclTranslation.append(sourceResult);
      oclTranslation.append("(Instances, Vars, Object),");
      oclTranslation.append("\n\t");

      String ccn = ((Class)refProp.eContainer()).getName();      
      //inConstraint ?? 
      oclTranslation.append("ocl_attributeCall(Instances,");
      oclTranslation.append('"');
      oclTranslation.append(ccn);
      oclTranslation.append('"');
      oclTranslation.append(",");
      oclTranslation.append('"');
      oclTranslation.append(propertyName);
      oclTranslation.append('"');
      oclTranslation.append(", Object, Result).\n");
    }
    else {
      predName = "nNavigation" + counter + constraintName;
      oclTranslation.append(predName);
      oclTranslation.append("(Instances, Vars, Result):-");
      oclTranslation.append("\n\t");
      oclTranslation.append(sourceResult);
      oclTranslation.append("(Instances, Vars, Value1),");
      oclTranslation.append("\n\t");

      Association assoc = refProp.getAssociation();
      String asName = assoc.getName();        
      String sourceRole = "";
      String destinationRole = "";
      if (assoc.getMemberEnds() != null) 
        for (Property assocEnd : assoc.getMemberEnds()) 
          if (assocEnd.getName() != propertyName)
            sourceRole = assocEnd.getName();
          else
            destinationRole = assocEnd.getName();
      oclTranslation.append("ocl_navigation(Instances,");
      oclTranslation.append('"');
      oclTranslation.append(asName);
      oclTranslation.append('"');
      oclTranslation.append(",");
      oclTranslation.append('"');
      oclTranslation.append(sourceRole);
      oclTranslation.append('"');
      oclTranslation.append(",");
      oclTranslation.append('"');
      oclTranslation.append(destinationRole);
      oclTranslation.append('"');
      oclTranslation.append(", Value1, Result).\n");            
    }
    return predName;
  }  

  @Override
  public String visitIteratorExp(IteratorExp<Classifier, Parameter> callExp) {
    String sourceResult = safeVisit(callExp.getSource());

    List<String> variableResults;
    List<Variable<Classifier, Parameter>> variables = callExp.getIterator();

    if (variables.isEmpty()) {
      variableResults = Collections.emptyList();
    } else {
      variableResults = new java.util.ArrayList<String>(variables.size());
      for (Variable<Classifier, Parameter> iterVar : variables) {
        varStack.push(iterVar.getName());  
        variableResults.add(safeVisit(iterVar));
      }
    }
    String bodyResult = safeVisit(callExp.getBody());
    if (!variables.isEmpty())            
      for (int i = 0; i < variables.size(); i++) 
        varStack.pop();    
    return handleIteratorExp(callExp, sourceResult, variableResults, bodyResult);
  }
   
  @Override
  protected String handleIteratorExp(IteratorExp<Classifier, Parameter> callExp, String sourceResult, List<String> variableResults, String bodyResult) {    
    String itName = callExp.getName();
    String type = itName.equalsIgnoreCase("forAll") ? "Boolean" : getType(callExp);
    
    ++counter;
    String predName = "n" + itName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Value1),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    if (itName.equals("collect") || itName.equals("select") || itName.equals("reject") || itName.equals("collectNested") ||  itName.equals("sortedBy")) {  
      oclTranslation.append(type); 
      oclTranslation.append("_"); 
    }
    else
      oclTranslation.append("col_"); 
    oclTranslation.append(itName);
    oclTranslation.append("(Instances, Vars, Value1, ");
    oclTranslation.append(bodyResult);
    oclTranslation.append(", Result).\n");
    return predName;
  }

  @Override
  protected String handleConstraint(Constraint constraint, String specificationResult) {
    return "";
  }

  @Override
  protected String handleExpressionInOCL(ExpressionInOCL<Classifier, Parameter> callExp, String contextResult, String resultResult, List<String> parameterResults, String bodyResult) {
    String predName = constraintName.substring(0,1).toLowerCase().concat(constraintName.substring(1, constraintName.length()));
    setConstraintFirstPredicate(bodyResult); 
    oclTranslation.append(predName);
    oclTranslation.append("(Instances):-");
    oclTranslation.append("\n\t");
    oclTranslation.append(bodyResult);
    oclTranslation.append("(Instances, [], Result),");
    oclTranslation.append("\n\t");
    oclTranslation.append("Result #=1.\n");    
    return oclTranslation.toString();     
  }
  
  private void setConstraintFirstPredicate(String firstPredicate) {
    this.firstPredicate = firstPredicate;
  }
  
  public String getConstraintFirstPredicate() {
    return firstPredicate;
  }

  private String getType(TypedElement<Classifier> exp) {
    String type = getName(exp.getType());
    if (type.toLowerCase().contains("int"))
      return "int";
    if (type.toLowerCase().contains("bool"))
      return "boolean";
    if (type.toLowerCase().contains("real"))
      return "real";
    if (type.toLowerCase().contains("set"))
      return "set";
    if (type.toLowerCase().contains("bag"))
      return "bag";
    if (type.toLowerCase().contains("seq"))
      return "seq";
    return type;    
  }

  private String getType(OperationCallExp<Classifier, Operation> exp) {
    Classifier sourceType = exp.getSource().getType();
    Classifier resultType = null;
    if (sourceType instanceof PredefinedType) 
      resultType = TypeUtil.getResultType(exp, env, sourceType, exp.getReferredOperation(), exp.getArgument());
    return getName(resultType);
  }

  private String getName(Object named) {
    String res = (uml == null) ? XML_NULL_PLACEHOLDER : uml.getName(named);
    if (res == null) {
      res = XML_NULL_PLACEHOLDER; 
    }
    return res; 
  }
  
  private String getCSPOpName(String name, String type, int arguments) {
    if (name.equals("<"))
      name = "less_than";
    else if (name.equals(">"))
      name = "greater_than";
    else if (name.equals("<="))
      name = "less_equal";
    else if (name.equals(">="))
      name = "greater_equal";
    else if (name.equals("="))
      name = "equals";
    else if (name.equals("<>"))
      name = "not_equals";
    else if (name.equals("/"))
      name = "division";
    else if (name.equals("+"))
      name = "plus";
    else if (name.equals("*"))
      name = "times";
    else if (name.equals("-") && type.toLowerCase().contains("set"))
      name = "set_difference";
    else if (name.equals("-") && arguments == 1)
      name = "binary_minus";
    else if (name.equals("-") && arguments == 0)
      name = "unary_minus";
    return name;
  }  
    
  @Override
  protected String handleOperationCallExp(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp);
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    if (opCSPName.equals("allInstances"))
      return transAllInstancesOp(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("atPre"))
      return "ZERO_PARAMETERS_AT_PRE";
    if (opCSPName.equals("plus") || opCSPName.equals("times") || opCSPName.equals("division") || opCSPName.equals("div") ||
        opCSPName.equals("mod") || opCSPName.equals("min") || opCSPName.equals("max") || opCSPName.equals("binary_minus"))
      return trans2ParamsArithRelOp(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("abs") || opCSPName.equals("floor") || opCSPName.equals("round") || opCSPName.equals("unary_minus"))
      return trans1ParamArithOp(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("greater_than") || opCSPName.equals("less_than") || opCSPName.equals("greater_equal") || opCSPName.equals("less_equal"))
      return trans2ParamsArithRelOp(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("equals") || opCSPName.equals("not_equals")) {
      if (opType.equals("Boolean") || opType.equals("Integer") || opType.equals("String") || opType.equals("Real") || opType.equals("Date"))
        return trans2ParamsArithRelOp(callExp, sourceResult, argumentResults);
      return trans2ParamsEqOverObjects(callExp, sourceResult, argumentResults);
    }
    if (opCSPName.equals("and") || opCSPName.equals("or") || opCSPName.equals("xor") || opCSPName.equals("implies"))
      return trans2ParamsLogicOp(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("not"))
      return trans1ParamLogicOp(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("size") || opCSPName.equals("isEmpty") || opCSPName.equals("notEmpty") || opCSPName.equals("sum") ||
        opCSPName.equals("flatten") || opCSPName.equals("asSet") || opCSPName.equals("asOrderedSet") || opCSPName.equals("asSequence") ||
        opCSPName.equals("asBag") || opCSPName.equals("first") || opCSPName.equals("last"))
      return transNoParamsOverCollections(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("excludes") || opCSPName.equals("includes") || opCSPName.equals("excludesAll") || opCSPName.equals("includesAll") ||
        opCSPName.equals("product") || opCSPName.equals("count") || opCSPName.equals("indexOf") || opCSPName.equals("append") ||
        opCSPName.equals("prepend") || opCSPName.equals("at") || opCSPName.equals("set_difference") || opCSPName.equals("symmetricDifference") ||
        opCSPName.equals("union") || opCSPName.equals("intersection") || opCSPName.equals("including") || opCSPName.equals("excluding"))
      return trans1ParamOverCollections(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("insertAt") || opCSPName.equals("subSequence") || opCSPName.equals("subOrderedSet"))
      return trans2ParamsOverCollections(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("toInteger") || opCSPName.equals("toReal"))
      return transNoParamsOverStrings(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("concat"))
      return trans1ParamOverStrings(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("substring"))
      return trans2ParamsOverStrings(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("oclIsUndefined"))
      return transOclIsUndefined(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("oclIsKindOf") || opCSPName.equals("oclIsTypeOf") || opCSPName.equals("oclAsType"))
      return trans1ParamWithType(callExp, sourceResult, argumentResults);
    if (opCSPName.equals("oclIsNew"))
      return transOclIsNew(callExp, sourceResult, argumentResults);
    return "";    
  }
  
  /***
   * TO DO
   * @param callExp
   * @param sourceResult
   * @param argumentResults
   * @return
   */
  private String transNoParamsOverStrings(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    return "";
  }

  /***
   * TO DO
   * @param callExp
   * @param sourceResult
   * @param argumentResults
   * @return
   */
  private String trans1ParamOverStrings(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    return "";
  }

  /***
   * TO DO
   * @param callExp
   * @param sourceResult
   * @param argumentResults
   * @return
   */
  private String trans2ParamsOverStrings(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    return "";
  }

  /***
   * TO DO
   * @param callExp
   * @param sourceResult
   * @param argumentResults
   * @return
   */
  private String transOclIsUndefined(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    return "";
  }

  private String trans1ParamArithOp(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, Vars, ");
    oclTranslation.append(sourceResult);
    oclTranslation.append("\", Result).\n");
    return predName;
  }
  
  private String trans2ParamsEqOverObjects(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Obj1),");
    oclTranslation.append("\n\t");
    oclTranslation.append(argumentResults.get(0));
    oclTranslation.append("(Instances, Vars, Obj2),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, Obj1, \"");
    oclTranslation.append(argumentResults.get(1));
    oclTranslation.append("\", Obj2, \"");
    oclTranslation.append(uml.getName(op.getOwnedParameters().get(0).getType()));
    oclTranslation.append("\", Result).\n");
    return predName;
  }
  
  private String trans1ParamLogicOp(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, Vars, ");
    oclTranslation.append(sourceResult);
    oclTranslation.append(", Result).\n");
    return predName;
  }  
  
  private String trans2ParamsLogicOp(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());    
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, Vars, ");
    oclTranslation.append(sourceResult);
    oclTranslation.append(", ");
    oclTranslation.append(argumentResults.get(0)); 
    oclTranslation.append(", Result).\n");
    return predName;
  }
  
  private String transNoParamsOverCollections(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Value1),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opType.toLowerCase());
    oclTranslation.append("_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Value1, Result).\n");
    return predName;
  }
  
  private String trans1ParamOverCollections(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Value1),");
    oclTranslation.append("\n\t");
    oclTranslation.append(argumentResults.get(0));
    oclTranslation.append("(Instances, Vars, Value2),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opType.toLowerCase());
    oclTranslation.append("_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Value1, Value2, Result).\n");
    return predName;
  }
  
  private String trans2ParamsOverCollections(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Value1),");
    oclTranslation.append("\n\t");
    oclTranslation.append(argumentResults.get(0));
    oclTranslation.append("(Instances, Vars, Value2),");
    oclTranslation.append("\n\t");
    oclTranslation.append(argumentResults.get(1));
    oclTranslation.append("(Instances, Vars, Value3),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opType.toLowerCase());
    oclTranslation.append("_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Value1, Value2, Value3, Result).\n");
    return predName;
  }

  private String trans1ParamWithType(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Object),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, Object, \"");
    oclTranslation.append(opType.toLowerCase());
    oclTranslation.append("\", Result).\n");
    return predName;
  }
  
  private String transOclIsNew(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");    
    oclTranslation.append("\n\t");
    oclTranslation.append(sourceResult);
    oclTranslation.append("(Instances, Vars, Object),");
    oclTranslation.append("\n\t");
    oclTranslation.append("getOid(Object, Oid),");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, \"");
    oclTranslation.append(opType.toLowerCase());
    oclTranslation.append("\", Oid, Result).\n");
    return predName;
  }
  
  private String transAllInstancesOp(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = getType(callExp.getSource());    
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);
    
    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, _, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, ");
    oclTranslation.append('"');
    oclTranslation.append(opType); //CCN????
    oclTranslation.append('"');
    oclTranslation.append(", Result).\n");    
    return predName;
  }
  
  private String trans2ParamsArithRelOp(OperationCallExp<Classifier, Operation> callExp, String sourceResult, List<String> argumentResults) {
    Operation op = callExp.getReferredOperation();
    String opName = getName(op);
    String opType = opName.equals("allInstances") ? "" : getType(callExp.getSource());
    int params = 0;
    if (op.getOwnedParameters() != null)
      params = op.getOwnedParameters().size();
    String opCSPName = getCSPOpName(opName, opType, params);

    ++counter;
    String predName = "n" + opCSPName + counter + constraintName;
    oclTranslation.append(predName);
    oclTranslation.append("(Instances, Vars, Result):-");
    oclTranslation.append("\n\t");
    oclTranslation.append("ocl_");
    oclTranslation.append(opType.toLowerCase()); 
    oclTranslation.append("_");
    oclTranslation.append(opCSPName);
    oclTranslation.append("(Instances, Vars, ");
    oclTranslation.append(sourceResult);
    oclTranslation.append(", ");
    oclTranslation.append(argumentResults.get(0));
    oclTranslation.append(", Result).\n");
    return predName;
  }   
}
