/**
 *  LICENSE
 * 
 *  This file is part of Flyve MDM Inventory Library for Android.
 *
 *  Inventory Library for Android is a subproject of Flyve MDM.
 *  Flyve MDM is a mobile device management software.
 * 
 *  Flyve MDM is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  Flyve MDM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @author    Rafael Hernandez - <rhernandez@teclib.com>
 *  @author    Ivan del Pino    - <idelpino@teclib.com>
 *  @copyright Copyright Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/flyve-mdm/android-inventory-library
 *  @link      http://flyve.org/android-inventory-library/
 *  @link      https://flyve-mdm.com
 *  ---------------------------------------------------------------------
 */

package org.flyve.inventory.categories;

import java.util.ArrayList;
import java.util.List;

public class CategoryValue {

    private String value;
    private String jsonName;
    private String xmlName;
    private Boolean isPrivate;
    private Boolean hasCDATA;
    private Category category;
    private List<String> values;

    /** Normal category
     * @param value
     * @param xmlName
     * @param jsonName
     */
    public CategoryValue(String value, String xmlName, String jsonName) {
        if(value==null) {
            value = "";
        }

        if(xmlName==null) {
            xmlName = "";
        }

        if(jsonName==null) {
            jsonName = "";
        }

        if (value.contains("<") || value.contains(">")) {
            value = value.replaceAll("[<>]", "");
        }

        this.value = value;
        this.jsonName = jsonName;
        this.xmlName = xmlName;
        this.isPrivate = false;
        this.hasCDATA = false;
    }

    /** Insert list of values to the Category
     * @param values
     * @param xmlName
     * @param jsonName
     */
    public CategoryValue(List<String> values, String xmlName, String jsonName) {
        if (values == null) {
            values = new ArrayList<>();
        }

        if (xmlName == null) {
            xmlName = "";
        }

        if (jsonName == null) {
            jsonName = "";
        }

        for (int i = 0; i < values.size(); i++) {
            if (values.contains("<") || values.contains(">")) {
                values.add(i, values.get(i).replaceAll("[<>]", ""));
            }
        }

        this.values = values;
        this.jsonName = jsonName;
        this.xmlName = xmlName;
        this.isPrivate = false;
        this.hasCDATA = false;
    }

    /** Embed an category inside in another category
     * @param category instance of the same class CategoryValue
     */
    public CategoryValue(Category category){
        this.category = category;
    }

    public CategoryValue(String value, String xmlName, String jsonName, Boolean isPrivate, Boolean hasCDATA) {
        if(value==null) {
            value = "";
        }

        if(xmlName==null) {
            xmlName = "";
        }

        if(jsonName==null) {
            jsonName = "";
        }

        if (value.contains("<") || value.contains(">")) {
            value = value.replaceAll("[<>]", "");
        }

        this.value = value;
        this.jsonName = jsonName;
        this.xmlName = xmlName;
        this.isPrivate = isPrivate;
        this.hasCDATA = hasCDATA;
    }

    public CategoryValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getJsonName() {
        return jsonName;
    }

    public String getXmlName() {
        return xmlName;
    }

    public Boolean isPrivate() {
        return isPrivate;
    }

    public Boolean hasCDATA() {
        return hasCDATA;
    }

    public Category getCategory() {
        return category;
    }

    public List<String> getValues() {
        return values;
    }
}
