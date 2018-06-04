function SimpleHtmlBuilder() {
    this._retval = "";
}

SimpleHtmlBuilder.prototype.createModalIconLine = function (iconName, fieldName, fieldValue) {
    this.beginRow();
    this.beginColumn("6");
    this.addMaterialIcon(iconName);
    this.addStrongText(fieldName);
    this.closeDiv();
    this.beginColumnAlignedToIcon("6");
    this.addText(fieldValue);
    this.closeDiv();
    this.endRow();
    var retval = this.getString();
    this.clear();
    return retval;
};

SimpleHtmlBuilder.prototype.createTitleLineWithStrongText = function (title, field) {
    this.beginRow();
    this.beginColumn("12");
    this.addStrongText(title);
    this.addLeftPaddedText(title.length, 20, field);
    this.closeDiv();
    this.endRow();
    var retval = this.getString();
    this.clear();
    return retval;
};

SimpleHtmlBuilder.prototype.getSelfContainedHeading = function (headingSize, text) {
    return "<h" + headingSize + " class='blue-text'>" + text + "</h" + headingSize + ">";
};

SimpleHtmlBuilder.prototype.createHeading = function (headingSize, text) {
    this._retval += "<h" + headingSize + ">" + text + "</h" + headingSize + ">";
};

SimpleHtmlBuilder.prototype.addText = function (text) {
    this._retval += text;
};

SimpleHtmlBuilder.prototype.addLeftPaddedText = function (fieldLength, length, text) {
    if (!(fieldLength > length)) {
        while (fieldLength < length) {
            this._retval += " ";
            fieldLength++;
        }
    }
    this._retval += text;
};

SimpleHtmlBuilder.prototype.beginColumn = function (size) {
    this._retval += "<div class='col " + size + "'>";
};

SimpleHtmlBuilder.prototype.beginColumnAlignedToIcon = function(size) {
    this._retval += "<div class='col " + size + " align-material-text-to-icon' style='padding-top:11.5px'>";
};

SimpleHtmlBuilder.prototype.addMaterialIcon = function (icon) {
    this._retval += "<i class='material-icons prefix small'>" + icon + "</i>";
};

SimpleHtmlBuilder.prototype.addStrongText = function (text) {
    this._retval += "<strong style='vertical-align: text-bottom'>" + text + "</strong>";
};

SimpleHtmlBuilder.prototype.clear = function () {
    this._retval = "";
};

SimpleHtmlBuilder.prototype.getString = function () {
    return this._retval;
};

SimpleHtmlBuilder.prototype.beginRow = function () {
    this._retval += "<div class='row '>";
};

SimpleHtmlBuilder.prototype.endRow = function () {
    this.closeDiv();
};

SimpleHtmlBuilder.prototype.closeDiv = function () {
    this._retval += "</div>";
};
