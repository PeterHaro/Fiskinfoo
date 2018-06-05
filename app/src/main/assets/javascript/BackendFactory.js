function BackendFactory() {}
BackendFactory.createBackend = function (backendType) {
    switch (backendType) {
        case Backend.Type.ANDROID:
            return new AndroidBackend();
            break;
        case "IOS":
            break;
        case Backend.Type.COMPUTER:
            return new ComputerBackend();
        default:
            console.log(backendType);
            console.log("Unknown backend type. Please validate the backend communication interface, and the value passed to the backend factory");
    }
};
