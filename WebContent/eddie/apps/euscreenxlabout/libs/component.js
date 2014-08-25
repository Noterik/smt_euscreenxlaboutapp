var Component = function(){
	console.log("Component()");
	this.waitingMessages = [];
	this._bindEvents();
};

Component.prototype.putMsg = function(command){
	var argsStart = command.originalMessage.indexOf("(");
	var fn = command.originalMessage.substring(0, argsStart);
		
	if(typeof this[fn] == "function"){
		this[fn](command.content);
	}
};
Component.prototype._bindEvents = function () {
    var self = this;
    for (var event in this.events) {
        var splits = event.split(" ");
        var actionsStr = splits[0];
        splits.splice(0, 1);
        var selector = splits.join(" ");
        var actions = actionsStr.split(",");

        var callback = this.events[event];

        jQuery(selector).on(actions.join(" "), (function (callback) {
            return function (event) {
                callback.apply(self, [event, self]);
            }
        })(callback));
    }
};
