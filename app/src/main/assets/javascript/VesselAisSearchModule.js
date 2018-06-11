class VesselAisSearchModule {
    constructor() {
    }

    setVesselData(vessels) {
        this.vessels = new Map(vessels.map((i) => [i.values_.Name, i]));// vessels.reduce((map, obj) => (map[obj.values_.Name] = obj, map), {});
        this.vesselNames = [...this.vessels.keys()];
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
}