import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CRC {
    public CRC(String divisor) {
        this.divisor = divisor;
    }

    private String divisor;

    public int getFrameDataSize() {
        return frameDataSize;
    }

    private int frameDataSize = 100;

    public String getCRC(String message){
        String messageWithEndBits = message + IntStream.range(0, divisor.length() - 1)
                .mapToObj(x -> "0")
                .collect(Collectors.joining());
        return getCRC(new ArrayList<>(messageWithEndBits.chars().mapToObj(e -> (char) e).collect(Collectors.toList())));
    }

    public String appendCRC(String message){
        return message + getCRC(message);
    }

    private String getCRC(ArrayList<Character> message){

        while(message.get(0) == '0' && message.size() >= divisor.length()){
            message.remove(0);
        }

        if(message.size() + 1 == divisor.length()){
            return message.stream().map(Object::toString).collect(Collectors.joining());
        }

        for(int i = 0; i < divisor.length(); i++){
            if(message.get(i) == divisor.charAt(i)){
                message.set(i, '0');
            }
            else {
                message.set(i, '1');
            }
        }

        return getCRC(message);
    }


    public boolean checkCorrectnessData(String message){
        return checkCorrectnessData(new ArrayList<>(message.chars().mapToObj(e -> (char) e).collect(Collectors.toList())));
    }

    private boolean checkCorrectnessData(ArrayList<Character> message){

        while(message.get(0) == '0' && message.size() >= divisor.length()){
            message.remove(0);
        }

        if(message.size() + 1 == divisor.length()){
            for(int i = 0; i < message.size() - 1; i++){
                if(message.get(i) != '0'){
                    return false;
                }
            }
            return true;
        }

        for(int i = 0; i < divisor.length(); i++){
            if(message.get(i) == divisor.charAt(i)){
                message.set(i, '0');
            }
            else {
                message.set(i, '1');
            }
        }

        return checkCorrectnessData(message);
    }


    private ArrayList<Character> appendCRC(ArrayList<Character> message){
        String crc = getCRC(message);
        for (char ch: crc.toCharArray()) {
            message.add(ch);
        }
        return message;
    }

    public String removeCRC(String message){
        return message.substring(0, message.length() - divisor.length() + 1);
    }

    public String stretchBites(String message){
        return stretchBites(new ArrayList<>(message.chars().mapToObj(e -> (char) e).collect(Collectors.toList())));
    }

    private String stretchBites(ArrayList<Character> message){
        int j = 0;
        int i = 0;

        while(message.size() > i){
            if(message.get(i) == '1'){
                j++;
            }
            else{
                j = 0;
            }

            if(j == 5){
                j = 0;
                message.add(i + 1, '0');
                i++;
            }
            i++;
        }
        return message.stream().map(Object::toString).collect(Collectors.joining());
    }

    public String shortBites(String message){
        return shortBites(new ArrayList<>(message.chars().mapToObj(e -> (char) e).collect(Collectors.toList())));
    }

    private String shortBites(ArrayList<Character> message){
        int j = 0;
        int i = 0;

        while(message.size() > i){
            if(message.get(i) == '1'){
                j++;
            }
            else{
                j = 0;
            }

            if(j == 5 && message.size() > i + 1 && message.get(i + 1) == '0'){
                j = 0;
                message.remove(i + 1);
            }
            i++;
        }
        return message.stream().map(Object::toString).collect(Collectors.joining());
    }

    public String addStartFrame(String message) {
        return "01111110" + message;
    }

    public String addEndFrame(String message) {
        return message + "01111110";
    }

    public String removeStartFrame(String message){
        if(message.substring(0, 8).equals("01111110")){
            return message.substring(8, message.length());
        }
        return "";
    }

    public String removeEndFrame(String message){
        int i = 0;
        int j = 0;

        while(i < message.length()){
            if(message.charAt(i) == '1'){
                j++;
            }
            else {
                j = 0;
            }
            if(j == 6 && message.length() > i + 1 && message.charAt(i + 1) == '0') {
                return message.substring(0, i - 6);
            }
            i++;
        }
        return "";
    }
}
