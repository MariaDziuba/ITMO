package info.kgeorgiy.ja.dziuba.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;


import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StudentDB implements StudentQuery {

    private static final Function<Student, String> GET_FULL_NAME =
            student -> student.getFirstName() + " " + student.getLastName();

    private static final Comparator<Student> COMPARATOR_BY_ID =
            Comparator.comparingInt(Student::getId);

    private static final Comparator<Student> COMPARATOR_BY_NAME = (Comparator.comparing(Student::getLastName, Comparator.reverseOrder())).
            thenComparing(Student::getFirstName, Comparator.reverseOrder()).
            thenComparing(Student::getId);


    private static Stream<Student> searchingStream(Collection<Student> students,
                                                   Predicate<Student> predicate,
                                                   Comparator<Student> comparator) {
        return students
                .stream()
                .filter(predicate)
                .sorted(comparator);
    }

    private static <R> Stream<Student> searchingByKeyStream(Collection<Student> students,
                                                            Function<Student, R> mapper,
                                                            R key) {
        return searchingStream(
                students,
                student -> key.equals(mapper.apply(student)),
                COMPARATOR_BY_NAME);
    }


    private static <R> Stream<R> mappingStream(List<Student> students,
                                               Function<Student, ? extends R> mapper) {
        return students
                .stream()
                .map(mapper);
    }


    private static Stream<Student> sortingStream(Collection<Student> students, Comparator<Student> comparator) {
        return students
                .stream()
                .sorted(comparator);
    }


    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mappingStream(students, Student::getFirstName).collect(Collectors.toList());
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mappingStream(students, Student::getLastName).collect(Collectors.toList());
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return mappingStream(students, Student::getGroup).collect(Collectors.toList());
    }


    @Override
    public List<String> getFullNames(List<Student> students) {
        return mappingStream(students, GET_FULL_NAME).collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return mappingStream(students, Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students
                .stream()
                .max(COMPARATOR_BY_ID)
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortingStream(students, COMPARATOR_BY_ID).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortingStream(students, COMPARATOR_BY_NAME).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return searchingByKeyStream(students, Student::getFirstName, name).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return searchingByKeyStream(students, Student::getLastName, name).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName groupName) {

        return searchingByKeyStream(students, Student::getGroup, groupName).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName groupName) {
        return searchingStream(
                students,
                student -> groupName.equals(student.getGroup()),
                Comparator.naturalOrder()).collect(
                Collectors.toMap(
                        Student::getLastName, Student::getFirstName,
                        BinaryOperator.minBy(String::compareTo)));
    }
}