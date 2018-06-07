class VesselAisSearchModule {
    /*  constructor(vessels) {
          this.vessels = vessels.reduce((map, obj) => (map[obj.values_.name] = obj, map), {});
          this.vesselNames = [...this.vessels.keys()]; // TODO: Do me dynamically if the size is 2 large
       } */

    constructor() {
    }

    setVesselData(vessels) {
        this.vessels = new Map(vessels.map((i) => [i.values_.Name, i]));// vessels.reduce((map, obj) => (map[obj.values_.Name] = obj, map), {});
        this.vesselNames = [...this.vessels.keys()]; // TODO: Do me dynamically if the size is 2 large
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