function BackendFactory() {}
BackendFactory.createBackend = function (backendType) {
    switch (backendType) {
        case "ANDROID":
            break;
        case "IOS":
            break;
        case Backend.Type.COMPUTER:
            return new ComputerBackend();
        default:
            console.log("Unknown backend type. Please validate the backend communication interface, and the value passed to the backend factory");
    }
};
