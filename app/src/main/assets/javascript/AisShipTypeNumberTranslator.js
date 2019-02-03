var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var AisShipTypeNumberTranslator = function () {
    function AisShipTypeNumberTranslator() {
        _classCallCheck(this, AisShipTypeNumberTranslator);
    }

    _createClass(AisShipTypeNumberTranslator, null, [{
        key: "translateNumber",
        value: function translateNumber(number) {
            if (number === 30) {
                return "Fiskefartøy";
            }
            if (number === 31) {
                return "Taubåt"; // TOwing vessel
            }
            if (number === 32) {
                return "Taubåt";
            }
            if (number === 33) {
                return "Mudringsfartøy";
            }
            if (number === 34) {
                return "Dykkerfartøy";
            }
            if (number === 35) {
                return "Militært fartøy";
            }
            if (number === 36) {
                return "Seilbåt";
            }
            if (number === 37) {
                return "Fritidsbåt";
            }
            if (number === 51) {
                return "Søk og redningsfartøy";
            }
            return " ";
        }
    }]);

    return AisShipTypeNumberTranslator;
}();