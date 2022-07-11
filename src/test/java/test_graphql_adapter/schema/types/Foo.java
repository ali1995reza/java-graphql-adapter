/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test_graphql_adapter.schema.types;

import graphql_adapter.annotations.DefaultValue;
import graphql_adapter.annotations.GraphqlInputType;
import graphql_adapter.annotations.GraphqlObjectType;
import test_graphql_adapter.schema.parsers.CustomBooleanValueParser;
import test_graphql_adapter.schema.parsers.CustomIntValueParser;
import test_graphql_adapter.schema.parsers.CustomStringValueParser;

import java.util.Arrays;
import java.util.Objects;

@GraphqlInputType(name = "FooInput")
@GraphqlObjectType(name = "FooOutput")
public class Foo {

    private String stringValue;
    private long longValue;
    private int intValue;
    private int intValue2;
    private double doubleValue;
    private float floatValue;
    private byte byteValue;
    private short shortValue;
    private char charValue;
    private boolean booleanValue;
    private boolean booleanValue2;
    private int[] intArray;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foo foo = (Foo) o;
        return longValue == foo.longValue && intValue == foo.intValue && intValue2 == foo.intValue2 && Double.compare(foo.doubleValue, doubleValue) == 0 && Float.compare(foo.floatValue, floatValue) == 0 && byteValue == foo.byteValue && shortValue == foo.shortValue && charValue == foo.charValue && booleanValue == foo.booleanValue && booleanValue2 == foo.booleanValue2 && Objects.equals(stringValue, foo.stringValue) && Arrays.equals(intArray, foo.intArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(stringValue, longValue, intValue, intValue2, doubleValue, floatValue, byteValue, shortValue, charValue, booleanValue, booleanValue2);
        result = 31 * result + Arrays.hashCode(intArray);
        return result;
    }

    @Override
    public String toString() {
        return "Foo {" +
                "stringValue='" + stringValue + '\'' +
                ", longValue=" + longValue +
                ", intValue=" + intValue +
                ", intValue2=" + intValue2 +
                ", doubleValue=" + doubleValue +
                ", floatValue=" + floatValue +
                ", byteValue=" + byteValue +
                ", shortValue=" + shortValue +
                ", charValue=" + charValue +
                ", booleanValue=" + booleanValue +
                ", booleanValue2=" + booleanValue2 +
                ", intArray=" + Arrays.toString(intArray) +
                '}';
    }

    @DefaultValue("120")
    public byte getByteValue() {
        return byteValue;
    }

    public Foo setByteValue(byte byteValue) {
        this.byteValue = byteValue;
        return this;
    }

    @DefaultValue("c")
    public char getCharValue() {
        return charValue;
    }

    public Foo setCharValue(char charValue) {
        this.charValue = charValue;
        return this;
    }

    @DefaultValue("2123455500000.21")
    public double getDoubleValue() {
        return doubleValue;
    }

    public Foo setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
        return this;
    }

    @DefaultValue("15477542.236")
    public float getFloatValue() {
        return floatValue;
    }

    public Foo setFloatValue(float floatValue) {
        this.floatValue = floatValue;
        return this;
    }

    @DefaultValue("[1,2,3]")
    public int[] getIntArray() {
        return intArray;
    }

    public Foo setIntArray(int[] intArray) {
        this.intArray = intArray;
        return this;
    }

    @DefaultValue("1")
    public int getIntValue() {
        return intValue;
    }

    public Foo setIntValue(int intValue) {
        this.intValue = intValue;
        return this;
    }

    @DefaultValue(value = "100", valueParser = CustomIntValueParser.class)
    public int getIntValue2() {
        return intValue2;
    }

    public Foo setIntValue2(int intValue2) {
        this.intValue2 = intValue2;
        return this;
    }

    @DefaultValue("9223372036854775807")
    public long getLongValue() {
        return longValue;
    }

    public Foo setLongValue(long longValue) {
        this.longValue = longValue;
        return this;
    }

    @DefaultValue("20000")
    public short getShortValue() {
        return shortValue;
    }

    public Foo setShortValue(short shortValue) {
        this.shortValue = shortValue;
        return this;
    }

    @DefaultValue(value = "anything", valueParser = CustomStringValueParser.class)
    public String getStringValue() {
        return stringValue;
    }

    public Foo setStringValue(String stringValue) {
        this.stringValue = stringValue;
        return this;
    }

    @DefaultValue("true")
    public boolean isBooleanValue() {
        return booleanValue;
    }

    public Foo setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
        return this;
    }

    @DefaultValue(value = "true", valueParser = CustomBooleanValueParser.class)
    public boolean isBooleanValue2() {
        return booleanValue2;
    }

    public Foo setBooleanValue2(boolean booleanValue2) {
        this.booleanValue2 = booleanValue2;
        return this;
    }
}
