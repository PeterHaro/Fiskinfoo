class VesselAisSearchModule {
    constructor() {
        this.attachTools = this.attachTools.bind(this);
        this.waitFor = this.waitFor.bind(this);
        this.hasVesselData = false;
    }

    setVesselData(vessels) {
        this.vessels = new Map(vessels.map((i) => [i.values_.Name, i]));// vessels.reduce((map, obj) => (map[obj.values_.Name] = obj, map), {});
        this.vesselNames = [...this.vessels.keys()];
        this.hasVesselData = true;
    }

    getVesselMap() {
        return this.vessels;
    }

    getVesselObject() {
        var retval = {};
        for (const [key, value] of this.vessels.entries()) {
            retval[key] = null;
        }
        //console.log(JSON.stringify(retval));
        return retval;
    }

    getVesselNames() {
        return this.vesselNames;
    }

    getVessel(name) {
        return this.vessels.get(name);
    }

    _attachTools(tools) {
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

    waitFor(condition, callback) {
        if (!condition()) {
            window.setTimeout(this.waitFor.bind(null, condition, callback), 100);
        } else {
            callback();
        }
    }

    attachTools(tools) {
        this.waitFor(() => this.hasVesselData, () => this._attachTools(tools));
    }
}