package tests.T1.schema;

import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlQuery;
import tests.Randomer;

import java.util.ArrayList;
import java.util.List;

@GraphqlQuery
public class Query {

    @GraphqlField
    public IntList getList(@GraphqlArgument(argumentName = "period") IntPeriodScalar periodScalar) {

        List<Integer> list = new ArrayList<>();
        periodScalar.forEach(list::add);

        return IntList.of(list);

    }

    @GraphqlField
    public List<List<Integer>> multiplyMatrix(@GraphqlArgument(argumentName = "m1", nullable = false) List<List<Integer>> first,
                                              @GraphqlArgument(argumentName = "m2", nullable = false) List<List<Integer>> second) {
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < first.size(); i++) {
            List<Integer> firstRow = first.get(i);
            List<Integer> secondRow = second.get(i);
            List<Integer> resultRow = new ArrayList<>();
            result.add(resultRow);
            for (int j = 0; j < firstRow.size(); j++) {
                resultRow.add(firstRow.get(j) * secondRow.get(j));
            }
        }

        return result;
    }


    @GraphqlField
    public UserInterface getUser(@GraphqlArgument(argumentName = "user", nullable = false) InputUser user) {
        if (user.getType() == UserType.NORMAL) {
            return NormalUser.create(user.getName());
        }
        return AdminUser.create(user.getName());
    }

    @GraphqlField
    public Vehicle getVehicle(@GraphqlArgument(argumentName = "isCar") Boolean isCar) {
        if (isCar == null) {
            isCar = false;
        }
        if (isCar) {
            Car car = new Car();
            car.setModel(Randomer.random("Ferrari", "Lamborghini", "Bugatti"));
            car.setProduceYear(Randomer.random(1998, 2001, 2012, 2021));
            return car;
        } else {
            Bus bus = new Bus();
            bus.setModel(Randomer.random("Benz", "Volvo", "Nissan"));
            bus.setSize(Randomer.random(10, 20, 30, 40));
            bus.setProduceYear(Randomer.random(1998, 2001, 2012, 2021));
            return bus;
        }
    }
}
