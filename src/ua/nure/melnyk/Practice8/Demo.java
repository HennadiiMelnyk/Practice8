package ua.nure.melnyk.Practice8;

import java.sql.SQLException;
import java.util.List;
import ua.nure.melnyk.Practice8.db.DBManager;
import ua.nure.melnyk.Practice8.db.entity.Group;
import ua.nure.melnyk.Practice8.db.entity.User;

public class Demo {

    private static <T> void printList(List<T> list) {
        for (T element : list) {
            System.out.println(element);
        }
    }

    public static void main(String[] args) throws SQLException {
        // users  ==> [ivanov]; groups ==> [teamA]

        DBManager dbManager = DBManager.getInstance();


        // Part 1
        dbManager.insertUser(User.createUser("petrov"));
        dbManager.insertUser(User.createUser("obama"));
        //dbManager.insertUser(User.createUser("ivanov"));
        printList(dbManager.findAllUsers());
        // users  ==> [ivanov, petrov, obama]

        System.out.println("===========================");

        // Part 2
       // dbManager.insertGroup(Group.createGroup("teamA"));
        dbManager.insertGroup(Group.createGroup("teamB"));
        dbManager.insertGroup(Group.createGroup("teamC"));
        printList(dbManager.findAllGroups());
        // users  ==> [TeamA, TeamB, TeamC]

        System.out.println("===========================");


       // Part 3

        User userPetrov = dbManager.getUser("petrov");
        User userIvanov = dbManager.getUser("ivanov");
        User userObama = dbManager.getUser("obama");

        Group teamA = dbManager.getGroup("teamA");
        Group teamB = dbManager.getGroup("teamB");
        Group teamC = dbManager.getGroup("teamC");




// method setGroupsForUser must implement transaction!
        dbManager.setGroupsForUser(userIvanov, teamA);
        dbManager.setGroupsForUser(userPetrov, teamA, teamB);
        dbManager.setGroupsForUser(userObama, teamA, teamB, teamC);

        for (User user : dbManager.findAllUsers()) {
            printList(dbManager.getUserGroups(user));
            System.out.println("~~~~~");
        }

       /* Метод DBManager#setGroupsForUser должен реализовывать транзакцию.
        Грамотно реализовать логику commit / rollback транзакции.
        Метод DBManager#getUserGroups возвращает объект java.util.List<Group>.*/
        System.out.println("===========================");

        //Part 4

// on delete cascade!
        dbManager.deleteGroup(teamA);
       /* Метод DBManager#deleteGroup удаляет группу по имени.
                Все дочерние записи из таблицы users_groups также должны быть удалены.
                последнее реализовать с помощью каскадных ограничений ссылочной целостности: ON DELETE CASCADE.
*/
        System.out.println("===========================");

        //Part 5

        teamC.setName("teamX");
        dbManager.updateGroup(teamC);

        printList(dbManager.findAllGroups());
    }

}