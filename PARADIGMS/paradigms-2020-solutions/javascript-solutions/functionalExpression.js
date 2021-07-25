"use strict";

const op = (operator) => {
    return (...operands) => (...values) => {
        let res = [];
        for (let arg of operands) {
            res.push((arg(...values)));
        }
        return operator(...res);
    }
};

const variable = (name) => (...values) => values[VARS.get(name)];

const cnst = (value) => () => value;

let cube = op((x) => Math.pow(x, 3));

let cuberoot = op((x) => Math.cbrt(x));

const add = op((x, y) => x + y);
const subtract = op((x, y) => x - y);
const multiply = op((x, y) => x * y);
const divide = op((x, y) => x / y);
const negate = op((x) => -x);


const VARS = new Map([
    ["x", 0],
    ["y", 1],
    ["z", 2]
]);