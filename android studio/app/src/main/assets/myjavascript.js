'use strict';

Blockly.JavaScript['control_wait'] = function(block) {
  var number_control_time = block.getFieldValue('control_time');
  var code = 'move.wait('+number_control_time+');\n';
  return code;
};

//运动
Blockly.JavaScript['move_go_back_time'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  var text_move_time = block.getFieldValue('move_time');
  // TODO: Assemble JavaScript into code variable.
  var code = 'move.goBackTime('+dropdown_go_or_back+','+text_move_speed+','+text_move_time+');\n';
  return code;
};
Blockly.JavaScript['move_clockwise'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  var text_move_time = block.getFieldValue('move_time');
  // TODO: Assemble JavaScript into code variable.
  var code = 'move.clockwise('+dropdown_go_or_back+','+text_move_speed+','+text_move_time+');\n';
  return code;
};
Blockly.JavaScript['move_go_back'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  // TODO: Assemble JavaScript into code variable.
  var code = 'move.goBack('+dropdown_go_or_back+','+text_move_speed+');\n';
  return code;
};
Blockly.JavaScript['move_direction'] = function(block) {
  var dropdown_go_or_back = block.getFieldValue('go_or_back');
  var text_move_speed = block.getFieldValue('move_speed');
  // TODO: Assemble JavaScript into code variable.
 var code = 'move.direction('+dropdown_go_or_back+','+text_move_speed+');\n';
  return code;
};
Blockly.JavaScript['move_stop'] = function(block) {
  var code = 'move.stop();\n';
  return code;
};

// LED
Blockly.JavaScript['open_led'] = function(block) {
  var on_or_off = block.getFieldValue('on_or_off');
  var code = 'move.openLed('+on_or_off+');\n';
  return code;
};
Blockly.JavaScript['state_led'] = function(block) {
  var code = 'move.stateLed();\n';
  return code;
};



