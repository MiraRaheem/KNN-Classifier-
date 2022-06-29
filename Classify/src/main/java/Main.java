import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Main {

    static Map<Integer, Vector> TrainSet = new TreeMap<Integer, Vector>();
    static Map<Integer, Vector> TestSet = new TreeMap<Integer, Vector>();
    static Map<Integer, Vector> TrainSet_C = new TreeMap<Integer, Vector>();
    static Map<Integer, Vector> TestSet_C = new TreeMap<Integer, Vector>();
    static Map<String, Integer> classifyAs = new TreeMap<String, Integer>();
    static int k;
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter number of K: ");
        k = Integer.parseInt(input.nextLine());
        // Divide file into test and train
        DivideFile();
        KNN_Pre_Train_and_Test();
        KNN_Predict();
        //ClassifyTest();

    }

    public static void DivideFile() throws IOException {
        //training set
        File myFile = new File("C://Users//MIRA//Desktop//Y4 T1//DM//Assignment3/cardata.xlsx");
        FileInputStream fis = new FileInputStream(myFile);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = mySheet.iterator();
        Vector<Vector<String>> bookdata = new Vector<>();
        int ProcessID = 0;

        for (int i = 0; i < 1290; i++) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            Vector<String> v = new Vector<String>();
            String ProcessName = "";
            String Atr = "";
            for (int j = 0; j < 7; j++) {
                Cell cell = cellIterator.next();
                Atr = cell.toString();
                v.add(Atr);
                //System.out.println(i + " " + cell.toString() + " " + cell.getColumnIndex() + " " + j);

            }

            bookdata.add(v);
            TrainSet.put(ProcessID, v);
            ProcessID++;


        }

        //System.out.println(bookdata);


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("train");
        int rowCount = 0;
        for (Vector aBook : bookdata) {
            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }


        try (FileOutputStream outputStream = new FileOutputStream("C://Users//MIRA//Desktop//Y4 T1//DM//Assignment3/Train.xlsx")) {
            workbook.write(outputStream);
        }
        bookdata.clear();

        // Test set
        for (int i = 1290; i < 1720; i++) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            Vector<String> v = new Vector<String>();
            String ProcessName = "";
            String Atr = "";
            for (int j = 0; j < 7; j++) {
                Cell cell = cellIterator.next();
                Atr = cell.toString();
                v.add(Atr);
                //System.out.println(i + " " + cell.toString() + " " + cell.getColumnIndex() + " " + j);

            }

            bookdata.add(v);
            TestSet.put(ProcessID, v);
            ProcessID++;


        }

        //System.out.println(bookdata);


        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("test");
        rowCount = 0;
        for (Vector aBook : bookdata) {
            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }


        try (FileOutputStream outputStream = new FileOutputStream("C://Users//MIRA//Desktop//Y4 T1//DM//Assignment3/Test.xlsx")) {
            workbook.write(outputStream);
        }

    }

    public static void KNN_Pre_Train_and_Test() {
        Map<String, Integer> feature1 = new TreeMap<String, Integer>();
        feature1.put("vhigh", 1);
        feature1.put("high", 2);
        feature1.put("med", 3);
        feature1.put("low", 4);

        Map<String, Integer> feature2 = new TreeMap<String, Integer>();
        feature2.put("vhigh", 1);
        feature2.put("high", 2);
        feature2.put("med", 3);
        feature2.put("low", 4);

        Map<String, Integer> feature3 = new TreeMap<String, Integer>();
        feature3.put("2.0", 2);
        feature3.put("3.0", 3);
        feature3.put("4.0", 4);
        feature3.put("5more", 5);

        Map<String, Integer> feature4 = new TreeMap<String, Integer>();
        feature4.put("2.0", 2);
        feature4.put("4.0", 3);
        feature4.put("more", 4);

        Map<String, Integer> feature5 = new TreeMap<String, Integer>();
        feature5.put("small", 1);
        feature5.put("med", 2);
        feature5.put("big", 3);

        Map<String, Integer> feature6 = new TreeMap<String, Integer>();
        feature6.put("low", 1);
        feature6.put("med", 2);
        feature6.put("high", 3);
        //unacc, acc, good, vgood

        classifyAs.put("unacc", 1);
        classifyAs.put("acc", 2);
        classifyAs.put("good", 3);
        classifyAs.put("vgood", 4);


        Vector<String> valuesInRow = new Vector<>();
        Vector<Integer> intValuesInRow = new Vector<>();

        //prepare Train
        int counter_Train = 1;
        for (Map.Entry<Integer, Vector> entry : TrainSet.entrySet()) {
            valuesInRow = entry.getValue();
            //System.out.println(valuesInRow);
            for (int i = 0; i < valuesInRow.size(); i++) {
                if (i == 0) {
                    int val = feature1.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 1) {
                    int val = feature2.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 2) {
                    int val = feature3.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 3) {
                    int val = feature4.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 4) {
                    int val = feature5.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 5) {
                    int val = feature6.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 6) {
                    int val = classifyAs.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                }


            }
            TrainSet_C.put(counter_Train, intValuesInRow);
            counter_Train++;
            //System.out.println(intValuesInRow);
            intValuesInRow = new Vector<>();
        }

        //prepare Test
        int counter_Test = 1;
        for (Map.Entry<Integer, Vector> entry1 : TestSet.entrySet()) {
            valuesInRow = entry1.getValue();
            for (int i = 0; i < valuesInRow.size(); i++) {
                if (i == 0) {
                    int val = feature1.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 1) {
                    int val = feature2.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 2) {
                    int val = feature3.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 3) {
                    int val = feature4.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 4) {
                    int val = feature5.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 5) {
                    int val = feature6.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                } else if (i == 6) {
                    int val = classifyAs.get(valuesInRow.get(i));
                    intValuesInRow.add(val);
                }
            }
            TestSet_C.put(counter_Test, intValuesInRow);
            counter_Test++;
            ;
            intValuesInRow = new Vector<>();
        }

    }

    public static void KNN_Predict() {
        //Get Distance
        double accuracy = 0.0;
        Map<Double, Integer> distance = new TreeMap<Double, Integer>();
        for (Map.Entry<Integer, Vector> entry : TestSet_C.entrySet()) {
            //test vector
            Vector<Integer> test_Vector = entry.getValue();

            for (Map.Entry<Integer, Vector> entry1 : TrainSet_C.entrySet()) {
                //train vector
                Vector<Integer> train_Vector = entry1.getValue();
                //Ecludien
                Double dist = 0.0;
                for (int i = 0; i < test_Vector.size() - 1; i++) {
                    dist += (test_Vector.get(i) - train_Vector.get(i)) * (test_Vector.get(i) - train_Vector.get(i));
                }
                dist = Math.sqrt(dist);
                distance.put(dist, entry1.getKey());

            }
            //System.out.println(distance);

            //Classify
            //1. Get 5 least distances and their classes
            Map<String, Integer> Nearest_points_classes = new HashMap<>();
            //unacc, acc, good, vgood
            Nearest_points_classes.put("unacc", 0);
            Nearest_points_classes.put("acc", 0);
            Nearest_points_classes.put("good", 0);
            Nearest_points_classes.put("vgood", 0);
            int j = 0;
            for (Map.Entry<Double, Integer> entry3 : distance.entrySet()) {
                if (j < k) {
                    Vector<String> n = TrainSet.get(entry3.getValue() - 1);
                    //System.out.println("n:" + n);

                    int count;
                    if (n.get(6).equals("unacc")) {
                        count = Nearest_points_classes.get("unacc");
                        count++;
                        Nearest_points_classes.put("unacc", count);
                    } else if (n.get(6).equals("acc")) {
                        count = Nearest_points_classes.get("acc");
                        count++;
                        Nearest_points_classes.put("acc", count);
                    } else if (n.get(6).equals("good")) {
                        count = Nearest_points_classes.get("good");
                        count++;
                        Nearest_points_classes.put("good", count);
                    } else if (n.get(6).equals("vgood")) {
                        count = Nearest_points_classes.get("vgood");
                        count++;
                        Nearest_points_classes.put("vgood", count);
                    }

                    //System.out.println(n.get(6));
                    j++;
                }
            }
            //System.out.println(Nearest_points_classes);
            int max = 0;
            String predicted = null;
            for (Map.Entry<String, Integer> e : Nearest_points_classes.entrySet()) {
                if (e.getValue() > max) {
                    max = e.getValue();
                    predicted = e.getKey();
                }
            }
            System.out.println("predicted class for" + entry.getValue() + "is: " + predicted);
            if (classifyAs.get(predicted) == test_Vector.get(6)) {
                accuracy++;
            }
        }
        System.out.println("Accuracy= " + accuracy / TestSet_C.size());
    }
}