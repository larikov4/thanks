package com.komandda;

/**
 * Created by Yevhen on 04.06.2016.
 */
public class Literature {

    public void main(String[] args) {

        int k = 0;
        System.out.println(++k + ". " + getHardCopyResourceName("Java 2: практ. рук.", "Мн.: УниверсалПресс", 2005, 400, new Author("И", "Блинов"), new Author("В", "Романчик")));
        System.out.println(++k + ". " + getHttpResourceName("Сайт Spring Framework", "spring.io"));
        System.out.println(++k + ". " + getHardCopyResourceName("Архитектура корпоративных программных приложений", "М.: Вильямс", 2007, 544, new Author("М", "Фаулер"), new Author("Д", "Райс")));
        System.out.println(++k + ". " + getHttpResourceName("Сайт Microsoft", "microsoft.com"));
        System.out.println(++k + ". " + getHttpResourceName("Технологии для эффективного управления", "bogdanov-associates.com"));
        System.out.println(++k + ". " + getHttpResourceName("Сайт Microsoft Office", "products.office.com"));
        System.out.println(++k + ". " + getHardCopyResourceName("Управление проектами в Microsoft Project 2010", "СПб: Питер", 2011, 127, new Author("А", "Просницкий")));
        System.out.println(++k + ". " + getHttpResourceName("About Google tables", "google.com.ua/intl/en/sheets/about"));
        System.out.println(++k + ". " + getHardCopyResourceName("UML Основы", "СПб: Символ-Плюс", 2002, 192, new Author("M", "Фаулер"), new Author("K", "Скотт ")));
        System.out.println(++k + ". " + getHardCopyResourceName("UML 2.0. Объектно-ориентированное моделирование и разработка", "СПб: Питер", 2007, 544, new Author("Дж", "Рамбо ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Effective Java. Second Edition ", "К.: Вид-во Courier in Stoughton, Massachusetts", 2008, 369, new Author("J", "Bloch  ")));
        System.out.println(++k + ". " + getHttpResourceName("Oracle Database SQL Language Reference", "docs.oracle.com/cd/E16655_01/server.121/e17209.pdf"));
        System.out.println(++k + ". " + getHardCopyResourceName("SQL", "К.: Вид-во \"ЛОРИ\"", 2003, 644, new Author("М", "Грабер   ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Язык запросов SQL. Учебный курс", "СПб: Питер", 2006, 416, new Author("Ф", "Андон     "), new Author("В", "Резниченко      ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Паттерны проектирования", "СПб: Питер", 2011, 656, new Author("Э", "Фримен   "), new Author("К", "Сьерра   "), new Author("Б", "Бейтс    ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Чистый код. Создание, анализ и рефакторинг", "СПб: Питер", 2010, 464, new Author("Р", "Мартин    ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Філософія Java. Бібліотека програміста. 4-е изд.", "СПб: Питер", 2013, 640, new Author("Б", "Еккель   ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Java. Полное руководство, 8-е изд.", "М.: Вильямс", 2012, 1104, new Author("Г", "Шилдт   ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Spring в действии", "М.: ДМК Пресс", 2013, 752, new Author("К", "Уоллс   ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Изучаем программирование на JavaScript", "СПб: Питер", 2016, 640, new Author("Э", "Робсон      "), new Author("Э", "Фримен       ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Изучаем HTML, XHTML и CSS 2-е изд.", "СПб: Питер", 2016, 720, new Author("Э", "Робсон      "), new Author("Э", "Фримен       ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Новая большая книга CSS", "СПб: Питер", 2016, 720, new Author("Д", "Макфарланд       ")));
        System.out.println(++k + ". " + getHardCopyResourceName("Шаблоны тестирования xUnit: рефакторинг кода тестов", "М.: Вильямс", 2015, 832, new Author("Дж", "Месарош    ")));





    }

    private static String getHttpResourceName(String name, String site){
        name = name.trim();
        site = site.trim();
        return name
                + " [Електронний ресурс] / портал "
                + site.split("\\\\")[0] + ": " + getFirstLetterUpperCase(site.split("\\.")[0])
                + ". — Режим доступу: www/URL: http://"
                + site
                + "/ — 31.05.2016 р. — Заг. з екрану.";
    }

    private static String getHardCopyResourceName(String name, String publisher, int year, int pagesAmount, Author... authors) {
        // publicshers      K.: Кн. палата України        СПб: Питер             М.: ДМК Пресс   Мн.: УниверсалПресс  Вильямс

        name = name.trim();
        publisher = publisher.trim();
        String result = "";
        Author firstAuthor = authors[0];
        result += firstAuthor.getSurname() + ", " + firstAuthor.getName() + ". ";
        result += name + " [Текст] / ";
        for(Author author : authors) {
            result += author.getName() + ". " + author.getSurname() + ", ";
        }
        result = result.substring(0, result.length()-1);
        return result += " — " + publisher + ", " + year + ". — " + pagesAmount + " c.";


    }

//    9.	Еккель, Б. Філософія Java. Бібліотека програміста. 4-е изд. [Текст] / Б. Еккель, пер. з англ. Е. Матвеев — СПб: Питер, 2013. – 640 с.


    private static class Author {
        private String name;
        private String surname;

        public Author(String name, String surname) {
            name = name.trim();
            surname = surname.trim();
            this.name = name;
            this.surname = surname;

        }

        public String getName() {
            return name;
        }


        public String getSurname() {
            return surname;
        }
    }

    private static String getFirstLetterUpperCase(String s) {
        return s.toUpperCase().charAt(0) + s.substring(1);
    }
}
