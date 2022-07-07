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

package tests.T1.schema;

import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlMutation;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@GraphqlMutation
public class TestMutation implements MutationInterface {

    @Override
    public String encodeToBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    @GraphqlField
    public int[][][] combineInto3DMatrix(@GraphqlArgument(name = "a") int[][] a, List<List<Integer>> b) {
        int[][][] combined = new int[2][][];
        combined[0] = a;
        combined[1] = new int[b.size()][];
        for (int i=0;i<b.size();i++) {
            List<Integer> list = b.get(i);
            int[] array = new int[list.size()];
            for(int j=0;j<list.size();j++) {
                array[j] = list.get(j);
            }
            combined[1][i] = array;
        }
        return combined;
    }

    @GraphqlField
    public int[] listToArray(List<Integer> list) {
        if(list == null) {
            return null;
        }
        int[] array = new int[list.size()];
        for(int i=0;i<list.size();i++ ){
            array[i] = list.get(i);
        }
        return array;
    }
}
