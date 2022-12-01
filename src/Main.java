import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String IN_FILE_TXT = "src\\inFile.txt";// ссылка на сайт с музыкой
    private static final String OUT_FILE_TXT = "src\\outFile.txt";//ссылки на скачивание музыки
    private static final String PATH_TO_MUSIC = "src\\Music\\music";//название и где хранится

    public static void main(String[] args) {
        String Url;

        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));//открытие потока чтения файла с ссылкой на сайт
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {//открытие потока записи найденных ссылок
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String result;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));//чтение сайта
                }
                Pattern email_pattern = Pattern.compile("href=\"\\/\\/mp3uks.ru\\/mp3\\/files\\/(.+-mp3).mp3");//регулярное выражение для поиска ссылок на скачивание
                Matcher matcher = email_pattern.matcher(result);//поиск
                int i = 0;
                while (matcher.find() && i < 5) {//5 - сколько музыки скачаем
                    outFile.write(matcher.group().replaceAll("href=\"", "")+"\n");//запись ссылок на скачивание
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader musicFile = new BufferedReader(new FileReader(OUT_FILE_TXT))) {//открытие потока чтения найденных ссылок на скачивание
            String music;
            int count = 0;
            try {
                while ((music = musicFile.readLine()) != null) {//построчно читаем файл с ссылками
                    downloadUsingNIO("https:" + music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");//передаём параметры(найденные ссылки, путь до музыки+название музыки в метод скачивания музыки)
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());//считывание по байтам
        FileOutputStream stream = new FileOutputStream(file);//запись музыки
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();//закрытие потоков
    }
}
