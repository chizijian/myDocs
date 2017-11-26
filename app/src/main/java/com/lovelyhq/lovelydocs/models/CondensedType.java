package com.lovelyhq.lovelydocs.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.lovelyhq.android.lovelydocs.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CondensedType implements Parcelable {
    public static final Creator<CondensedType> CREATOR = new Creator<CondensedType>() {
        public CondensedType createFromParcel(Parcel source) {
            return new CondensedType(source);
        }

        public CondensedType[] newArray(int size) {
            return new CondensedType[size];
        }
    };
    private String alias;
    private int icon;
    private List<String> names;

    static class CondensedTypeComparator implements Comparator<CondensedType> {
        CondensedTypeComparator() {
        }

        public int compare(CondensedType o1, CondensedType o2) {
            return o1.getAlias().compareTo(o2.getAlias());
        }
    }

    public CondensedType() {
        this.icon = 0;
    }

    public CondensedType(String alias) {
        this.icon = 0;
        this.alias = alias;
        this.icon = generateIcon();
    }

    public CondensedType(int icon, String alias, List<String> names) {
        this.icon = 0;
        this.icon = icon;
        this.alias = alias;
        this.names = names;
    }

    private CondensedType(Parcel in) {
        this.icon = 0;
        this.icon = in.readInt();
        this.alias = in.readString();
        this.names = new ArrayList();
        in.readList(this.names, List.class.getClassLoader());
    }

    public static List<CondensedType> condenseTypes(List<Type> types) {
        Map<String, List<String>> map = new HashMap();
        for (Type type : types) {
            if (map.containsKey(type.getAlias())) {
                ((List) map.get(type.getAlias())).add(type.getName());
            } else {
                List<String> newList = new ArrayList();
                newList.add(type.getName());
                map.put(type.getAlias(), newList);
            }
        }
        List<CondensedType> condensedTypes = new ArrayList();
        for (Entry<String, List<String>> entry : map.entrySet()) {
            CondensedType condensedType = new CondensedType((String) entry.getKey());
            condensedType.setNames(new ArrayList((Collection) entry.getValue()));
            condensedTypes.add(condensedType);
            Collections.sort(condensedTypes, new CondensedTypeComparator());
        }
        return condensedTypes;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getNames() {
        return this.names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    private int generateIcon() {
        if (this.alias.equals("Classes")) {
            return R.drawable.icon_alias_classes;
        }
        if (this.alias.equals("Attributes")) {
            return R.drawable.icon_alias_attributes;
        }
        if (this.alias.equals("Bindings")) {
            return R.drawable.icon_alias_bindings;
        }
        if (this.alias.equals("Categories")) {
            return R.drawable.icon_alias_categories;
        }
        if (this.alias.equals("Constants")) {
            return R.drawable.icon_alias_constants;
        }
        if (this.alias.equals("Constructors")) {
            return R.drawable.icon_alias_constructors;
        }
        if (this.alias.equals("Enums")) {
            return R.drawable.icon_alias_enums;
        }
        if (this.alias.equals("Events")) {
            return R.drawable.icon_alias_events;
        }
        if (this.alias.equals("Fields")) {
            return R.drawable.icon_alias_fields;
        }
        if (this.alias.equals("Functions")) {
            return R.drawable.icon_alias_functions;
        }
        if (this.alias.equals("Guides")) {
            return R.drawable.icon_alias_guides;
        }
        if (this.alias.equals("Macros")) {
            return R.drawable.icon_alias_macros;
        }
        if (this.alias.equals("Methods")) {
            return R.drawable.icon_alias_methods;
        }
        if (this.alias.equals("Namespaces")) {
            return R.drawable.icon_alias_namespaces;
        }
        if (this.alias.equals("Properties")) {
            return R.drawable.icon_alias_properties;
        }
        if (this.alias.equals("Protocols")) {
            return R.drawable.icon_alias_protocols;
        }
        if (this.alias.equals("Structs")) {
            return R.drawable.icon_alias_structs;
        }
        if (this.alias.equals("Types")) {
            return R.drawable.icon_alias_types;
        }
        if (this.alias.equals("Variables")) {
            return R.drawable.icon_alias_variables;
        }
        if (this.alias.equals("Index")) {
            return R.drawable.icon_alias_index;
        }
        if (this.alias.equals("Directives")) {
            return R.drawable.icon_alias_directives;
        }
        if (this.alias.equals("Exceptions")) {
            return R.drawable.icon_alias_exceptions;
        }
        if (this.alias.equals("Filters")) {
            return R.drawable.icon_alias_filters;
        }
        if (this.alias.equals("Libraries")) {
            return R.drawable.icon_alias_libraries;
        }
        if (this.alias.equals("Modules")) {
            return R.drawable.icon_alias_modules;
        }
        if (this.alias.equals("Operators")) {
            return R.drawable.icon_alias_operators;
        }
        if (this.alias.equals("Services")) {
            return R.drawable.icon_alias_services;
        }
        if (this.alias.equals("Settings")) {
            return R.drawable.icon_alias_settings;
        }
        if (this.alias.equals("Interfaces")) {
            return R.drawable.icon_alias_interfaces;
        }
        if (this.alias.equals("Packages")) {
            return R.drawable.icon_alias_packages;
        }
        if (this.alias.equals("Objects")) {
            return R.drawable.icon_alias_objects;
        }
        if (this.alias.equals("Traits")) {
            return R.drawable.icon_alias_traits;
        }
        if (this.alias.equals("Commands")) {
            return R.drawable.icon_alias_classes;
        }
        if (this.alias.equals("Builtins")) {
            return R.drawable.icon_alias_builtins;
        }
        if (this.alias.equals("Parameters")) {
            return R.drawable.icon_alias_parameters;
        }
        if (this.alias.equals("Words")) {
            return R.drawable.icon_alias_words;
        }
        if (this.alias.equals("Samples")) {
            return R.drawable.icon_alias_samples;
        }
        if (this.alias.equals("Sections")) {
            return R.drawable.icon_alias_sections;
        }
        if (this.alias.equals("Mixins")) {
            return R.drawable.icon_alias_mixins;
        }
        if (this.alias.equals("Styles")) {
            return R.drawable.logo_stylus;
        }
        if (this.alias.equals("Unions")) {
            return R.drawable.icon_alias_unions;
        }
        if (this.alias.equals("Files")) {
            return R.drawable.icon_alias_files;
        }
        if (this.alias.equals("Elements")) {
            return R.drawable.icon_alias_elements;
        }
        if (this.alias.equals("Options")) {
            return R.drawable.icon_alias_options;
        }
        if (this.alias.equals("Globals")) {
            return R.drawable.icon_alias_globals;
        }
        if (this.alias.equals("Callbacks")) {
            return R.drawable.icon_alias_classes;
        }
        if (this.alias.equals("Values")) {
            return R.drawable.icon_alias_variables;
        }
        if (this.alias.equals("Notations")) {
            return R.drawable.icon_alias_notations;
        }
        if (this.alias.equals("Errors")) {
            return R.drawable.icon_alias_errors;
        }
        if (this.alias.equals("Components")) {
            return R.drawable.icon_alias_classes;
        }
        if (this.alias.equals("Environments")) {
            return R.drawable.icon_alias_environments;
        }
        if (this.alias.equals("Annotations")) {
            return R.drawable.icon_alias_annotations;
        }
        if (this.alias.equals("Indirections")) {
            return R.drawable.icon_alias_interfaces;
        }
        if (this.alias.equals("Reports")) {
            return R.drawable.icon_alias_reports;
        }
        if (this.alias.equals("Variants")) {
            return R.drawable.icon_alias_variables;
        }
        if (this.alias.equals("_Struct")) {
            return R.drawable.icon_alias_structs;
        }
        if (this.alias.equals("Modifiers")) {
            return R.drawable.icon_alias_modifiers;
        }
        if (this.alias.equals("Shortcuts")) {
            return R.drawable.icon_alias_shortcuts;
        }
        if (this.alias.equals("Aliases")) {
            return R.drawable.icon_alias_aliases;
        }
        if (this.alias.equals("Tests")) {
            return R.drawable.icon_alias_tests;
        }
        if (this.alias.equals("Extensions")) {
            return R.drawable.icon_alias_environments;
        }
        if (this.alias.equals("Provisioners")) {
            return R.drawable.icon_alias_parameters;
        }
        if (this.alias.equals("Hooks")) {
            return R.drawable.icon_alias_hooks;
        }
        if (this.alias.equals("Resources")) {
            return R.drawable.icon_alias_reports;
        }
        return R.drawable.icon_alias_classes;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.icon);
        dest.writeString(this.alias);
        dest.writeList(this.names);
    }
}
