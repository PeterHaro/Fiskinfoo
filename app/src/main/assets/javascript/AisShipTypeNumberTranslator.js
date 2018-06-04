// https://help.marinetraffic.com/hc/en-us/articles/205579997-What-is-the-significance-of-the-AIS-Shiptype-number-
// TODO: Add multiple languages
class AisShipTypeNumberTranslator {
    static translateNumber(number) {
        if(number === 30) {
            return "Fiskefartøy"
        }
        if(number === 31) {
            return "Taubåt"; // TOwing vessel
        }
        if(number === 32) {
            return "Taubåt";
        }
        if(number === 33) {
            return "Mudringsfartøy";
        }
        if(number === 34) {
            return "Dykkerfartøy";
        }
        if(number === 35) {
            return "Militært fartøy";
        }
        if(number === 36) {
            return "Seilbåt";
        }
        if(number === 37) {
            return "Fritidsbåt";
        }
        if(number === 51) {
            return "Søk og redningsfartøy";
        }
        return " ";
    }
}