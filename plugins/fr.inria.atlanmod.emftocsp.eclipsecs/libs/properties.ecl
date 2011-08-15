%

% ECLiPSe libraries

%

:- lib(ic).      % Interval constraints

:- lib(apply).   % Using variables as functors



% Correctness properties



% strongSatisfiabilityConstraint(?Vars) :-

%    Constrains all cardinality variables in Vars to be strictly greater than 0 



strongSatisfiabilityConstraint(Vars):- 

   ( foreach(Vi,Vars) do Vi#>0 ).



% weakSatisfiabilityConstraint(?Vars) :-

%    Constrains the sum of all cardinality variables in Vars to be strictly 

%    greater than 0 



weakSatisfiabilityConstraint(Vars) :-

   sum(Vars) #> 0.



% liveliness(?Vars, +Typename):-

%    Constrains the cardinality of a given class to be greater than 0



livelinessConstraint(Vars, Typename) :-

   index(Typename, Index),

   nth1(Index, Vars, Card),

   Card #> 0.



% noConstraintSubsumption(?Instances, +Constraint1, +Constraint2) :-

%    The variables of Instances must be assigned in such a way that

%    Constraint1 is satisfied and Constraint2 is not satisfied



noConstraintSubsumption(Instances, Constraint1, Constraint2) :-

   apply(Constraint1, [Instances, [], TruthValue1]),

   apply(Constraint2, [Instances, [], TruthValue2]),

   TruthValue1 #= 1,

   TruthValue2 #= 0.



% noConstraintRedundancy(?Instances, +Constraint1, +Constraint2) :-

%    The variables of Instances must be assigned in such a way that

%    only Constraint1 or only Constraint2 is satisfied, but not both



noConstraintRedundancy(Instances, Constraint1, Constraint2) :-

   apply(Constraint1, [Instances, [], TruthValue1]),

   apply(Constraint2, [Instances, [], TruthValue2]),

   TruthValue1 #\= TruthValue2.