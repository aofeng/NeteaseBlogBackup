function getObjField() {
	var argsNum = arguments.length;
	var fieldExpression = "";
	if (argsNum == 1) {
		fieldExpression = arguments[1];
    } else if (argsNum >= 2) {
    	var objFields = new Array();
    	for(var index=0; index<argsNum; index++) {
    		objFields.push(arguments[index]);
    	}
    	fieldExpression = objFields.join('.');
    }
    return eval(fieldExpression);
}

var window = {};
var document = {};
var location = {};
var Date = {};
var UD = {};