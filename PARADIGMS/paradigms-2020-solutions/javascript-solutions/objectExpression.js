"use strict";

<<<<<<< HEAD
function Expression(constructor, args) {
    const tmp = Object.create(constructor.prototype);
    constructor.apply(tmp, args);
    return tmp;
}

function Const(value) {
    this.value = value;
}

Const.prototype = Object.create(Const.prototype);
Const.prototype.toString = function () {
    return this.value.toString();
};
Const.prototype.prefix = Const.prototype.toString;
Const.prototype.evaluate = function () {
    return this.value;
};

function Variable(name) {
    this.name = name;
}

Variable.prototype = Object.create(Variable.prototype);
Variable.prototype.toString = function () {
    return this.name;
};
Variable.prototype.prefix = Variable.prototype.toString;
Variable.prototype.evaluate = function (...values) {
    return values[VARS.get(this.name)];
};


function Op(...args) {
    this.operands = [].slice.call(args);
}

Op.prototype.toString = function () {
    return this.operands.join(" ") + " " + this.operator;
};
Op.prototype.prefix = function () {
    return "(" + this.operator + " " + this.operands.map(function (value) {
        return value.prefix()
    }).join(" ") + ")";
};
Op.prototype.evaluate = function (...args) {
    const res = this.operands.map((value) => value.evaluate(...args));
    return this._action(...res);
};


function OpDefine(maker, calc, operator) {
    this.constructor = maker;
    this._action = calc;
    this.operator = operator;
}

OpDefine.prototype = Op.prototype;

const abstractOp = (calculationAlg, operator) => {
    const result = function () {
        Op.apply(this, arguments);
    };
    result.prototype = new OpDefine(result, calculationAlg, operator);
    return result;
};


const
    Add = abstractOp(
        (a, b) => a + b, "+"
    ),
    Subtract = abstractOp(
        (a, b) => a - b, "-"),
    Multiply = abstractOp(
        (a, b) => a * b, "*"),
    Divide = abstractOp(
        (a, b) => a / b, "/"),
    Negate = abstractOp(
        (a) => -a, "negate"),
    ArcTan = abstractOp(
        (a) => Math.atan(a), "atan"),
    Min3 = abstractOp(
        (...operands) => Math.min(...operands), "min3"),
    Max5 = abstractOp(
        (...operands) => Math.max(...operands), "max5"),
    Exp = abstractOp((a) => Math.exp(a), "exp");


const
    OPERATORS = {
        "+": [Add, 2],
        "-": [Subtract, 2],
        "/": [Divide, 2],
        "*": [Multiply, 2],
        "negate": [Negate, 1],
        "atan": [ArcTan, 1],
        "exp": [Exp, 1],
        "min3": [Min3, 1],
        "max5": [Max5, 1]
    },

    VARS = new Map([
        ["x", 0],
        ["y", 1],
        ["z", 2]
    ]);

function ParserError(errorMsg) {
    this.message = errorMsg + " in " + exprs + " at index " + index;
}

ParserError.prototype = Error.prototype;
ParserError.prototype.name = "ParserError";
ParserError.prototype.constructor = ParserError;


let index = 0;
let exprs = "";
let result = [];

const skipWhitespaces = () => {
    while (index < exprs.length && /\s/.test(exprs.charAt(index))) {
        index++
    }
};

const getIdentifier = () => {
    if (!(/[A-Za-z]/.test(exprs.charAt(index)))) {
        throw new ParserError("Incorrect identifier \'" + exprs.charAt(index) + "\'");
    }
    let identifier = "";
    while (index < exprs.length && /\w/.test(exprs.charAt(index))) {
        identifier += exprs.charAt(index++);
    }
    return identifier;
};
 const getValue = () => {
    let val = "";
    if (exprs.charAt(index) === "-") {
        val += "-";
        index++;
    }
    while (index < exprs.length && /\d/.test(exprs.charAt(index))) {
        val += exprs.charAt(index++);
    }
    if (val !== "" && val !== "-") {
        return parseInt(val);
    }
    if (val === "-") {
        index--;
    }
    return undefined;
};

const parsePrefix = (expression) => {
    index = 0;
    result = [];
    exprs = expression;
    if (expression.length === 0) {
        throw new ParserError("Empty expression");
    }
    let braceBalance = 0;
    skipWhitespaces();
    while (index < exprs.length) {
        let curValue,
            curOperator,
            curIdentifier;
        if (exprs.charAt(index) === ")") {
            if (--braceBalance < 0) {
                throw new ParserError("Missing closing parenthesis");
            }
            let operands = [];
            while ((result[result.length - 1] !== "(") && !(result[result.length - 1] in OPERATORS)) {
                operands.push(result.pop());
            }
            if (result[result.length - 1] === "(") {
                throw new ParserError("Missing operator");
            }
            curOperator = result.pop();
            if (result.pop() !== "(") {
                throw new ParserError("Missing parenthesis");
            }
            if (operands.length > OPERATORS[curOperator][1]) {
                throw new ParserError("Too many operands, expected " + OPERATORS[curOperator][1] + " operands");
            } else if (operands.length < OPERATORS[curOperator][1]) {
                throw new ParserError("Missing operand");
            } else {
                result.push(Expression(OPERATORS[curOperator][0], operands.reverse()));
            }
            index++;
            if (braceBalance === 0) {
                break;
            }
        } else if (exprs.charAt(index) === "(") {
            result.push("(");
            index++;
            braceBalance++;
        } else if ((curValue = getValue()) !== undefined) {
            result.push(new Const(curValue));
        } else {
            if (exprs.charAt(index) in OPERATORS) {
                curOperator = exprs.charAt(index);
                index++;
            } else {
                curIdentifier = getIdentifier();
                if (curIdentifier in OPERATORS) {
                    curOperator = curIdentifier;
                }
            }
            if (curOperator !== undefined) {
                result.push(curOperator);
            } else if (VARS.has(curIdentifier)) {
                result.push(new Variable(curIdentifier));
                if (braceBalance === 0) {
                    break;
                }
            } else {
                index--;
                throw new ParserError("Incorrect identifier \'" + exprs.charAt(index) + "\'");
            }
        }
        skipWhitespaces();
    }
    skipWhitespaces();
    if (index !== exprs.length) {
        throw new ParserError("Unknown ending of correct expression");
    } else if (result.length > 1) {
        throw new ParserError("Incorrect parenthesis placement");
    } else if (braceBalance > 0) {
        throw new ParserError("Missing closing parenthesis");
    }
    return result[0];
};
=======
>>>>>>> a834497d1bd7f271eac9edc91fc915c4f708840c
