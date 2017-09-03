import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class Task1 {

    public static void main(String[] args) throws IOException {
        CRC crc  = new CRC("1011");
        String message;
        String inputContent = new String(Files.readAllBytes(Paths.get("./src/main/resources/crc_input")));
        String outputContent;
        int framesNumber = 0;

        new PrintWriter("./src/main/resources/crc_output").close();
        new PrintWriter("./src/main/resources/crc_all_frames").close();

        while (!inputContent.equals("")){
            framesNumber++;

//        ( -> )
        if(inputContent.length() >= crc.getFrameDataSize()){
            message = crc.appendCRC(inputContent.substring(0, crc.getFrameDataSize()));
            inputContent = inputContent.substring(crc.getFrameDataSize(), inputContent.length());
        }
        else{
            message = crc.appendCRC(inputContent);
            inputContent = "";
        }
            message = crc.stretchBites(message);
            message = crc.addStartFrame(message);
            message = crc.addEndFrame(message);

            try {
                Files.write(Paths.get("./src/main/resources/crc_all_frames"), (message + "\n").getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {;}

//        ( <- )
            message = crc.removeStartFrame(message);
            message = crc.removeEndFrame(message);
            message = crc.shortBites(message);
            if(crc.checkCorrectnessData(message)){
                message = crc.removeCRC(message);
                try {
                    Files.write(Paths.get("./src/main/resources/crc_output"), message.getBytes(), StandardOpenOption.APPEND);
                }catch (IOException e) {;}
            }

        }

        outputContent = new String(Files.readAllBytes(Paths.get("./src/main/resources/crc_output")));
        inputContent = new String(Files.readAllBytes(Paths.get("./src/main/resources/crc_input")));

        System.out.println("---  REPORT  ---");
        System.out.println("Frames sent: " + framesNumber);
        System.out.println("Input file and output file have the same content: " + outputContent.equals(inputContent));


        String sttr = "0111111001000111000011101111101001011110000001111001000111001111100111011000011100110111100000111110011010010111101111110";

        sttr = crc.removeStartFrame(sttr);
        sttr = crc.removeEndFrame(sttr);
        sttr = crc.shortBites(sttr);

        System.out.println(crc.checkCorrectnessData(sttr));

    }

}
