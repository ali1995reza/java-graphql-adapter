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

package tests.T1;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TestUtils {

    private static Boolean isParameterNamePresent = null;

    public static boolean isParameterNamePresent() {
        if (isParameterNamePresent == null) {
            try {
                isParameterNamePresent = String.class.getMethod("valueOf", char.class)
                        .getParameters()[0]
                        .isNamePresent();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }
        return isParameterNamePresent;
    }

    public static String base64Hash(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
