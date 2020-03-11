'use strict';

function execute(code) {
  try {
    eval(code);
  } catch (e) {
    if (e !== Infinity) {
      alert(e);
    }
  }
};