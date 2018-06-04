ol.control.LayerSwitcher = function (options) {
    var defaults = {};


    var element = document.createElement("div");
    ol.control.Control.call(this, {
        element: document.getElementById(this.element),
        target: this._options.target
    });
    this.mapListeners = [];
    this.hiddenClassName = 'ol-unselectable ol-control layer-switcher';
    if (this.isTouchDevice_()) {
        this.hiddenClassName += ' touch';
    }


};


ol.control.LayerSwitcher.prototype.enableTouchScroll_ = function (elm) {
    if (this.isTouchDevice_()) {
        var scrollStartPos = 0;
        elm.addEventListener("touchstart", function (event) {
            scrollStartPos = this.scrollTop + event.touches[0].pageY;
        }, false);
        elm.addEventListener("touchmove", function (event) {
            this.scrollTop = scrollStartPos - event.touches[0].pageY;
        }, false);
    }
};

/**
 * @private
 * @desc Determine if the current browser supports touch events. Adapted from
 * https://gist.github.com/chrismbarr/4107472
 */
ol.control.LayerSwitcher.prototype.isTouchDevice_ = function () {
    try {
        document.createEvent("TouchEvent");
        return true;
    } catch (e) {
        return false;
    }
};
