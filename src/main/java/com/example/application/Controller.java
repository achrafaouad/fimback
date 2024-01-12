package com.example.application;

import com.example.application.service.HeadServie;
import com.example.application.service.StationNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController

public class Controller {

    @Autowired
    private HeadServie headServie;

    @Autowired
    private StationNameService stationNameService;
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @PostMapping
    public ResponseEntity<LogResponse> processLogs(@RequestBody String filename) {

        System.out.println("hello");
        // Process the filename and generate logs
        logger.info("Processing logs for file: {}", filename);
        // ...
        String logs = "Logs for " + filename;
        String message = "Processing completed successfully.";

        LogResponse response = new LogResponse(logs, message);
        return ResponseEntity.ok(response);
    }

    public static class LogResponse {
        private String logs;
        private String message;

        // Constructors, getters, and setters

        public LogResponse(String logs, String message) {
            this.logs = logs;
            this.message = message;
        }

        public String getLogs() {
            return logs;
        }

        public void setLogs(String logs) {
            this.logs = logs;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    @GetMapping
    public String getting() {
        return "achraf";
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExportResponse> uploadAndExtractColumns(@RequestPart("file") MultipartFile file) {


        Integer colonneVoieIndice = 4;
//    public ResponseEntity<byte[]> uploadAndExtractColumns(@RequestPart("file") MultipartFile file) {
        StringBuilder logs = new StringBuilder();


        List<String[]> matrixList = new ArrayList<>();
        List<String[]> matrixList2 = new ArrayList<>();
        int rows = 14, columns = 12;


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.newFormat('.').withIgnoreSurroundingSpaces());

            System.out.println(csvParser);
            logs.append(csvParser + "\n");

            for (CSVRecord record : csvParser) {
                String[] row = new String[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    row[i] = record.get(i);
                }
                matrixList.add(row);
            }
            csvParser.close();
        } catch (IOException e) {
            // Handle exception
            e.printStackTrace();
        }


        // controle du fichier
        matrixList = this.removeLastEmptyElement(matrixList);
        matrixList2 = matrixList;
        matrixList = this.completeRow(matrixList);

        Object[][] comptage = matrixList.toArray(new Object[0][]);
        Object[][] comptage2 = matrixList2.toArray(new Object[0][]);


        // avec i et j
        rows = comptage.length;
        columns = comptage[0].length;
        System.out.println("nbr de ligne : " + rows + " , et nbr de colomns : " + columns);
        logs.append("nbr de ligne : " + rows + " , et nbr de colomns : " + columns + "\n");

        // Display comptage
        System.out.println("comptage is: ");
//printing comptage

        Object[][] convertion = new Object[rows][20];
        for (Object[] row : matrixList2) {
            for (Object column : row) {
                System.out.print(column + ", \t");
            }
        }


        String str1 = "34";
        String formatted1 = String.format("%04d", Integer.valueOf(str1));
        System.out.println(formatted1);




        int cellule = 0;
        int ii = 0;
        int jj = 0;
        int k = 0;
        int classe = 0;
        int resultat;
        int nbr_heures = 0;
        boolean fin_fichier = false;

        int maxVois = 0;


        //nombre max des voies
        for (int i = 0; i < rows; i++) {

            if (comptage[i][12] != "" && comptage[i][13] != "") {
                if (maxVois < Integer.parseInt((String) comptage[i][colonneVoieIndice])) {

                    maxVois = Integer.parseInt((String) comptage[i][colonneVoieIndice]);
                }


            }
        }


        int counterLines = 0;
        Object[] lastElement = null;
        boolean conditionMet = false;
        int indiceLast = 0;
        for (int i = 0; i < rows; i++) {
            if (!comptage[i][12].equals("") && !comptage[i][13].equals("")) {
                if (conditionMet) {
                    indiceLast = i-1;
                    break; // Exit the loop when the condition is met for the second time
                }
                conditionMet = true; // Set the flag to true when the condition is met for the first time

                System.out.println("achraf 2");


            }

            if (conditionMet) {
                lastElement = comptage2[i];

                counterLines++; // Increment the counter when the condition has been met at least once
            }
        }

        counterLines = counterLines-1;

        System.out.println("Number of lines between consecutive conditions: " + (counterLines));
        System.out.println("Max value of comptage[i][4]: " + maxVois);

        System.out.println("nombre d hours du dernier element  " + lastElement.length);

//  *****************************************************

//        extructing some elements


        System.out.println(Integer.parseInt((String) comptage[0][0]));
//        station
        Integer Station = Integer.parseInt((String) comptage[0][0]);
//   station code
       String stationCode  = stationNameService.get(Station);
        System.out.println(stationCode);


       String stationcompleatedFIle = String.format("%08d", Integer.valueOf((String) comptage[0][0]));
        System.out.println(stationcompleatedFIle);

//date debut enregistrement des données (jj/mm/aa)

      Integer minute =   Integer.parseInt((String) comptage[0][9]);
      Integer hour =   Integer.parseInt((String) comptage[0][8]);
      Integer day =   Integer.parseInt((String) comptage[0][7]);
      Integer month =   Integer.parseInt((String) comptage[0][6]);
      Integer year =   Integer.parseInt((String) comptage[0][5]);
        System.out.println(LocalDateTime.of(2000+year,month,day,hour,minute));

        LocalDateTime currentDateTime = LocalDateTime.of(2000+year,month,day,hour,minute);

        System.out.println(formatDateAsJjMmAa(LocalDateTime.of(2000+year,month,day,hour,minute)));

        String date_debut = formatDateAsJjMmAa(LocalDateTime.of(2000+year,month,day,hour,minute));

// hour
        String hour1 = "" + (String) comptage[0][8]+":"+(String) comptage[0][9];
        System.out.println(hour1);
        String hour_debut = hour1;
// number of hours

        System.out.println((counterLines-1) * 12 + lastElement.length);

       Integer hoursToAdd = (counterLines-1) * 12 + lastElement.length;

        LocalDateTime newDateTime = addHours(currentDateTime, hoursToAdd);
        LocalTime newTime = getTime(newDateTime);

        System.out.println("Current date and time: " + currentDateTime);
        System.out.println("New date and time after adding " + hoursToAdd + " hours: " + newDateTime);
        System.out.println("Corresponding time: " + newTime);
        String date_fin = formatDateAsJjMmAa(newDateTime);
        String hour_fin = ""+ newTime;
// nbr voie

        int nbrvoie  = maxVois;



//  *****************************************************


        for (int i = 0; i < rows; i++) {
            k = 0; // Initialize the counter for non-empty cells in the row to 0

            for (int j = 0; j < columns; j++) {
                if (comptage[i][j] != "") {
                    k++; // Counting the number of non-empty cells in the row
                }

                // Print the current row and column index (i and j) to the console

//                System.out.println(i + "/" + j);
//                logs.append(i + "/" + j + "\n");

                // Handling different cases based on the content of comptage[i][j]

                // Case 1: If the row represents a "poste" or "classe"
                if (comptage[i][12] != "" && comptage[i][13] != "") {
                    if (j == 0) {
                        // If j is 0, it means the cell is the first column, representing the "n° poste ou classe"
//                        convertion[0][classe] = comptage[i][j]; // Store the value in the convertion array
//                        convertion[2][classe] = i; // Store the row number in the convertion array
//                        convertion[3][classe] = classe; // Store the class number in the convertion array
                    }
                    if (j == 4) {
                        // If j is 4, it means the cell is the 5th column, representing the "n° de la voie"
//                        convertion[1][classe] = comptage[i][j]; // Store the value in the convertion array
                    }
                }
                // Case 2: If the row doesn't represent a "poste" or "classe"
                else {
                    if (comptage[i][j] != "") {
                        // If the cell is not empty, store the value in the convertion array
                        // and keep track of the column index (jj) where the non-empty cell was found
                        convertion[cellule + j][classe - 1] = comptage[i][j];
                        jj = j;
                    }
                }

                // Calculate the current index (resultat) in the convertion array
                resultat = cellule + j;

                // Print some information related to the current cell and classe to the console and logs
                System.out.println("cellule+j : " + resultat + "  ;classe :  " + classe + "  comptage[i][j] : " + comptage[i][j]);
                logs.append("cellule+j : " + resultat + "  ;classe :  " + classe + "  comptage[i][j] : " + comptage[i][j] + "/n");
            }

            // Check if it is the last row (fin_fichier will be true)
            if (comptage.length - 1 == i) {
                fin_fichier = true;
            }

            // Print the number of non-empty elements in the current row to the console and logs
            System.out.println(" nbr element  ligne =  " + (i) + " = " + k);
            logs.append(" nbr element  ligne =  " + (i) + " = " + k + "\n");

            // Increment the cellule index for the next row based on the number of non-empty cells in the current row
            cellule = cellule + jj + 1;

            // Check if the number of non-empty cells in the row is equal to 14
            if (k == 14) {
                // If true, it indicates a new classe or total section
                System.out.println(" detection classe / total ");
                logs.append(" detection classe / total " + "\n");

                // Increment the classe counter to indicate a new classe section
                classe = classe + 1;

                // Reset the cellule index to start at 6 again (as each classe section has 14 cells, 6 + 14 = 20)
                cellule = 0;
            }
            // Check if the number of non-empty cells in the row is equal to 12
            else if (k == 12) {
                // If true, it indicates a 12-hour counting
                System.out.println(" 12 de comptage  : " + k);
                logs.append(" 12 de comptage  : " + k + "\n");

                // Increment the total number of hours (each non-empty cell represents 1 hour)
                nbr_heures = nbr_heures + 12;

                // Check if it is not the last row and the next row represents a new classe (indicated by a non-empty cell in column 13)
                if (!fin_fichier && comptage[i + 1][13] != "") {
                    // Reset the cellule index to start at 6 again for the next classe
                    cellule = 0;
                }
            }
            // If the number of non-empty cells in the row is less than 12
            else {
                // It indicates a partial hour counting (e.g., less than 12 hours in the row)
                System.out.println(" moins de 12h de comptage  : " + k);
                logs.append(" 12 de comptage  : " + k + "\n");

                // Increment the total number of hours with the number of non-empty cells in the row
                nbr_heures = nbr_heures + k;

                // Reset the cellule index to start at 6 again for the next row
                cellule = 0;
            }

            // If it is not the first row and the current row represents a poste or classe (indicated by non-empty cells in columns 12 and 13)
            if (comptage[i][12] != "" && comptage[i][13] != "" && i != 0) {
                // Save the total number of hours for the previous classe (classe-2 because classe was already incremented)
//                convertion[4][classe - 2] = nbr_heures;
            }

            // If it is the last row, save the total number of hours for the last classe
            if (fin_fichier) {
//                convertion[4][classe - 1] = nbr_heures;
            }

            // Reset the number of hours to 0 if a new classe is detected
            if (k == 14) {
                nbr_heures = 0;
            }
        }

        // Display convertion
        StringBuilder exportedContent = new StringBuilder();

        exportedContent = this.headServie.gettingHead(nbrvoie,stationCode,date_debut,stationcompleatedFIle,hour_debut,date_fin,hour_fin);
        Object[][] conversion2 = this.convertRowsToArray(convertion,12);

        System.out.println("comptage is: ");

        for (Object[] row : conversion2) {
            String rowContent = "";
            for (Object column : row) {

                if(column != null){
                    System.out.print(column + "\t");
                    rowContent += (column + "\t");
                }


            }
            exportedContent.append(rowContent + "\n");

        }

        try {

            byte[] file1Bytes = exportedContent.toString().getBytes(StandardCharsets.UTF_8);

            byte[] file2Bytes = logs.toString().getBytes(StandardCharsets.UTF_8);

            // Create the ExportResponse object with the file content and file data
            ExportResponse exportResponse = new ExportResponse();
            exportResponse.setFile1Content(exportedContent);
            exportResponse.setFile2Content(logs);
            exportResponse.setFile1Bytes(file1Bytes);
            exportResponse.setFile2Bytes(file2Bytes);
            exportResponse.setFileName(stationCode +"000");

            // Return the ExportResponse as a ResponseEntity
            return ResponseEntity.ok(exportResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }


    public List<String[]> removeLastEmptyElement(List<String[]> matrixList) {
        List<String[]> modifiedList = new ArrayList<>();

        // Iterate over each row in the matrixList
        for (String[] row : matrixList) {
            int lastIndex = row.length - 1;

            // Check if the last element is an empty string
            if (lastIndex >= 0 && row[lastIndex].equals("")) {
                // Remove the last element
                row = Arrays.copyOf(row, lastIndex);
            }

            // Add the modified row to the modifiedList
            modifiedList.add(row);
        }

        return modifiedList;
    }


    public List<String[]> completeRow(List<String[]> matrixList) {
        List<String[]> modifiedList = new ArrayList<>();

        // Iterate over each row in the matrixList
        for (String[] row : matrixList) {
            // Check if the row has less than 14 elements
            if (row.length < 14) {
                // Create a new array with 14 elements
                String[] completedRow = new String[14];

                // Copy the existing elements to the new array
                System.arraycopy(row, 0, completedRow, 0, row.length);

                // Fill the remaining elements with empty strings
                for (int i = row.length; i < 14; i++) {
                    completedRow[i] = "";
                }

                // Use the completedRow instead of the original row
                modifiedList.add(completedRow);
            } else {
                // Row already has 14 elements, add it as is
                modifiedList.add(row);
            }
        }

        return modifiedList;
    }


    private String[][] removeRow(String[][] matrix, int rowIndex) {
        String[][] newMatrix = new String[matrix.length - 1][];
        int index = 0;

        for (int i = 0; i < matrix.length; i++) {
            if (i != rowIndex) {
                newMatrix[index++] = matrix[i];
            }
        }


        return newMatrix;
    }


    private String[][] popLastRow(String[][] matrix) {
        String[][] newMatrix = new String[matrix.length - 1][];
        for (int i = 0; i < newMatrix.length; i++) {
            newMatrix[i] = matrix[i];
        }
        return newMatrix;
    }



    public static Object[][] convertRowsToArray(Object[][] rows, int elementsPerRow) {

        // Remove rows with null as the first element
        rows = removeRowsWithNullFirstElement(rows);

        int totalElements = Arrays.stream(rows)
                .filter(row -> row[0] != null) // Exclude rows with null as the first element
                .mapToInt(row -> row.length)
                .sum();

        int totalRows = (totalElements + elementsPerRow - 1) / elementsPerRow;

        Object[][] convertion2 = new Object[totalRows][elementsPerRow];

        int rowIndex = 0;
        int columnIndex = 0;
        for (Object[] row : rows) {
            for (Object element : row) {
                convertion2[rowIndex][columnIndex] = element;
                columnIndex++;
                if (columnIndex == elementsPerRow) {
                    rowIndex++;
                    columnIndex = 0;
                    if (rowIndex == totalRows) {
                        break;
                    }
                }
            }
        }

        return convertion2;
    }



    public static Object[][] removeRowsWithNullFirstElement(Object[][] rows) {
        return Arrays.stream(rows)
                .filter(row -> row[0] != null)
                .toArray(Object[][]::new);
    }


    public  String formatDateAsJjMmAa(LocalDateTime dateTime) {
        // Create a DateTimeFormatter with the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Format the date and return the formatted string
        return dateTime.format(formatter);
    }


    public static LocalDateTime addHours(LocalDateTime dateTime, long hoursToAdd) {
        return dateTime.plusHours(hoursToAdd);
    }

    public static LocalTime getTime(LocalDateTime dateTime) {
        return dateTime.toLocalTime();
    }
}



