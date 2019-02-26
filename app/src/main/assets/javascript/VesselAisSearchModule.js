"use strict";

function _instanceof(left, right) { if (right != null && typeof Symbol !== "undefined" && right[Symbol.hasInstance]) { return right[Symbol.hasInstance](left); } else { return left instanceof right; } }

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

function _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _nonIterableSpread(); }

function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance"); }

function _iterableToArray(iter) { if (Symbol.iterator in Object(iter) || Object.prototype.toString.call(iter) === "[object Arguments]") return Array.from(iter); }

function _arrayWithoutHoles(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = new Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } }

function _classCallCheck(instance, Constructor) { if (!_instanceof(instance, Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var VesselAisSearchModule =
/*#__PURE__*/
function () {
  function VesselAisSearchModule() {
    _classCallCheck(this, VesselAisSearchModule);

    this.attachTools = this.attachTools.bind(this);
    this.waitFor = this.waitFor.bind(this);
    this.hasVesselData = false;
  }

  _createClass(VesselAisSearchModule, [{
    key: "setVesselData",
    value: function setVesselData(vessels) {
      this.vessels = new Map(vessels.map(function (i) {
        return [i.N.Name, i];
      })); // vessels.reduce((map, obj) => (map[obj.values_.Name] = obj, map), {});

      this.vesselNames = _toConsumableArray(this.vessels.keys());
      this.hasVesselData = true;
    }
  }, {
    key: "getVesselMap",
    value: function getVesselMap() {
      return this.vessels;
    }
  }, {
    key: "getVesselObject",
    value: function getVesselObject() {
      var retval = {};
      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = this.vessels.entries()[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var _step$value = _slicedToArray(_step.value, 2),
              key = _step$value[0],
              value = _step$value[1];

          retval[key] = null;
        }
      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }

      return retval;
    }
  }, {
    key: "getVesselNames",
    value: function getVesselNames() {
      return this.vesselNames;
    }
  }, {
    key: "getVessel",
    value: function getVessel(name) {
      return this.vessels.get(name);
    }
  }, {
    key: "_attachTools",
    value: function _attachTools(tools) {
      tools.forEach(function (tool) {
        if (this.vessels.get(tool.get("vesselname")) !== undefined) {
          if (this.vessels.get(tool.get("vesselname")).tools === undefined) {
            this.vessels.get(tool.get("vesselname")).tools = [tool];
          } else {
            this.vessels.get(tool.get("vesselname")).tools.push(tool);
          }
        }
      }.bind(this));
    }
  }, {
    key: "waitFor",
    value: function waitFor(condition, callback) {
      if (!condition()) {
        window.setTimeout(this.waitFor.bind(null, condition, callback), 100);
      } else {
        callback();
      }
    }
  }, {
    key: "attachTools",
    value: function attachTools(tools) {
      var _this = this;

      this.waitFor(function () {
        return _this.hasVesselData;
      }, function () {
        return _this._attachTools(tools);
      });
    }
  }]);

  return VesselAisSearchModule;
}();