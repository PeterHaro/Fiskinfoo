function SimpleHtmlBuilder() {
    this._retval = "";
}

SimpleHtmlBuilder.prototype.createModalIconLine = function (iconName, fieldName, fieldValue) {
    this.beginRow();
    this.beginColumn("4");
    this.addMaterialIcon(iconName);
    this.addStrongText(fieldName);
    this.closeDiv();
    this.beginColumnAlignedToIcon("8");
    this.addText(fieldValue);
    this.closeDiv();
    this.endRow();
    var retval = this.getString();
    this.clear();
    return retval;
};

SimpleHtmlBuilder.prototype.addModalIconLine = function (iconName, fieldName, fieldValue) {
    var retval = "";
    retval += "<div class='row '>";
    retval += "<div class='col m4 s" + "4" + "'>";
    retval += "<i class='material-icons prefix small'>" + iconName + "</i>";
    retval += "<strong style='vertical-align: text-bottom; white-space: pre;'>" + fieldName + "</strong>";
    retval += "</div>";
    retval += "<div class='col s" + "8" + " align-material-text-to-icon' style='padding-top:11.5px; white-space: pre;'>";
    retval += fieldValue;
    retval += "</div>";
    retval += "</div>";
    return retval;
};

SimpleHtmlBuilder.prototype.buildCollectionWithHeaderAndLinks = function (headerField, items, href) {
    this._retval = this.beginCollection("with-header");
    this._retval += this.addCollectionHeader(headerField);
    for (var i in items.tools) {
        this._retval += this.addCollectionItem(items.tools[i].get("tooltypename")); //TODO: REPLACE THIS WITH THE TRANSLATOR YOU HAVE AT HOME
    }
    var retval = this.getString();
    return retval;
};

/*
    retval += this._httpBuilder.createModalIconLine("date_range", "Tid i havet", feature.getTimePlacedInOcean());
    retval += this._httpBuilder.createModalIconLine("date_range", "Satt", feature.getFormattedTimeSetInOcean());
    retval += this._httpBuilder.createModalIconLine("place", "Posisjon", feature.getCoordinates());

 */

SimpleHtmlBuilder.prototype.buildInlineToolInfo = function (item) {
    var _feature = new BarentswatchApiObjectFactory().create(BarentswatchApiObjectTypes.TOOL);
    _feature.parseObject(item);
    var retval = "";
    retval += this.addModalIconLine("date_range", "Tid i havet", _feature.getTimePlacedInOcean());
    retval += this.addModalIconLine("date_range", "Satt", _feature.getFormattedTimeSetInOcean());
    retval += this.addModalIconLine("place", "Posisjon", _feature.getCoordinates());
    retval += "<a href=\"javascript:locateTool('" +_feature._vesselname + "','" + _feature._id + "'" + ");\" class=\"collection-item\">Se redskapet</a>";
    return retval;
};

SimpleHtmlBuilder.prototype.addCollapsibleItem = function (item) {
    this._retval += "<li>";
    this._retval += "<div class=\"collapsible-header\"><i class=\"material-icons\">arrow_drop_down</i>" + item.get("tooltypename") + "</div>"; //TODO: REPLACE THIS WIT TRANSLATOR
    this._retval += "<div class=\"collapsible-body\"><span>" +
        this.buildInlineToolInfo(item) +
        "</span></div>";
    this._retval += "</li>";
};

SimpleHtmlBuilder.prototype.buildCollapsible = function (items) {
    this._beginCollapsible("popout");
    for (var i in items) {
        this.addCollapsibleItem(items[i]);
    }
    this._retval += "</ul>";
    var retval = this.getString();
    this.clear();
    return retval;
};

SimpleHtmlBuilder.prototype.createTitleLineWithStrongText = function (title, field) {
    this.beginRow();
    this.mobileStrongTextColumn();
    this.addStrongText(title);
    this.closeDiv();
    this.textFieldMobileSupported();
    this.addText(field);
    this.closeDiv();
    this.endRow();
    var retval = this.getString();
    this.clear();
    return retval;
};

SimpleHtmlBuilder.prototype._beginCollapsible = function (extraClasses) {
    this._retval += "<ul class='collapsible ";
    if (extraClasses !== undefined && extraClasses !== null) {
        this._retval += extraClasses;
    }
    this._retval += "'>";
};

SimpleHtmlBuilder.prototype.addCollectionItem = function (itemField) {
    return "<li class='collection-item'>" + itemField + "</li>";
};

SimpleHtmlBuilder.prototype.addCollectionHeader = function (headerField) {
    return "<li class='collection-header'><h4>" + headerField + "</h4></li>";
};

SimpleHtmlBuilder.prototype.beginCollection = function (optionalCssClasses) {
    var baseCollectionString = "<ul class='collection ";
    if (optionalCssClasses !== undefined && optionalCssClasses !== null) {
        baseCollectionString += optionalCssClasses + "'";
    } else {
        baseCollectionString += "'";
    }
    return baseCollectionString;
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

SimpleHtmlBuilder.prototype.mobileStrongTextColumn = function () {
    this._retval += "<div class='col m6 s4" + "'>";
};

SimpleHtmlBuilder.prototype.textFieldMobileSupported = function () {
    this._retval += "<div class='col m6 s8" + "'>";
};


SimpleHtmlBuilder.prototype.beginColumn = function (size) {
    this._retval += "<div class='col m4 s" + size + "'>";
};

SimpleHtmlBuilder.prototype.beginColumnAlignedToIcon = function (size) {
    this._retval += "<div class='col s" + size + " align-material-text-to-icon' style='padding-top:11.5px; white-space: pre;'>";
};

SimpleHtmlBuilder.prototype.addMaterialIcon = function (icon) {
    this._retval += "<i class='material-icons prefix small'>" + icon + "</i>";
};

SimpleHtmlBuilder.prototype.addStrongText = function (text) {
    this._retval += "<strong style='vertical-align: text-bottom; white-space: pre;'>" + text + "</strong>";
};

SimpleHtmlBuilder.prototype.justifyText = function (text, length) {
    while (text.length < length) {
        text += " ";
    }
    return text
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
