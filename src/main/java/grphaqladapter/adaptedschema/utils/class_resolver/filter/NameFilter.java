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

package grphaqladapter.adaptedschema.utils.class_resolver.filter;

import java.util.regex.Pattern;

public class NameFilter implements ClassFilter {

    public static ClassFilter startWith(String str) {
        return new NameFilter("^" + str + ".*");
    }
    private final Pattern pattern;

    public NameFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    public NameFilter(String regx) {
        this(Pattern.compile(regx));
    }

    @Override
    public boolean accept(Class cls) {
        return pattern.matcher(cls.getName()).matches();
    }
}
