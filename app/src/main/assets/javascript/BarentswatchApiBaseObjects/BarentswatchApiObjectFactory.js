function BarentswatchApiObjectFactory() {
    this.create = function (type) {
        switch (type) {
            case BarentswatchApiObjectTypes.TOOL:
                return new Tool();
            case BarentswatchApiObjectTypes.SEABOTTOM_INSTALLATION:
                return new SeaBottomInstallation();
            case BarentswatchApiObjectTypes.JMESSAGE:
                return new JMessage();
            case BarentswatchApiObjectTypes.CORAL_REEF:
                return new CoralReef();
            case BarentswatchApiObjectTypes.COASTLINES_COD:
                return new CoastlinesCod();
            case BarentswatchApiObjectTypes.ICE_CONSENTRATION:
                return new IceConsentration();
            case BarentswatchApiObjectTypes.ONGOING_SEISMIC:
                return new OngoingSeismic();
            case BarentswatchApiObjectTypes.PLANNED_SEISMIC:
                return new PlannedSeismic();
            case BarentswatchApiObjectTypes.AIS:
                return new Ais();
            default:
                return null;
        }
    };
}