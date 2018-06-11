import com.google.common.primitives.Bytes;
import org.apache.commons.codec.digest.DigestUtils;
import org.paukov.combinatorics3.Generator;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static StringBuilder ap = new StringBuilder();
    private static Integer numberOfCPUs;
    private static long MAXCOMBOSPERITERATION = 0;
    private static boolean WANNACOUNT = false;
    private static String filename = "data.csv";

//TODO especificar o numero de threads


    public static void main(String args[]) throws IOException {

        int maxLetras;
        String palavra,aux;
        Long start;
        Long difference;
        Map<String,Object> outValues = new HashMap<>();
        String threads = "0";
        numberOfCPUs = Runtime.getRuntime().availableProcessors();
        Double speedUp;

        maxLetras = "zigzag".length(); // 27 min para 7 caracteres,« estimativa de 36 horas para 8 caracteres com 8 cores
        palavra = "zigzag";

        if(WANNACOUNT)
            MAXCOMBOSPERITERATION = 10000;
        else
            MAXCOMBOSPERITERATION = 0;


        questaoUm();


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Maximo de letras : ");
        maxLetras=Integer.parseInt(br.readLine());
        System.out.println();
        System.out.println("Hash : ");
        palavra = br.readLine();
        System.out.println();
        System.out.println("Quantas threads : ");
        threads = br.readLine();
        if(Integer.parseInt(threads)>1)
        {   numberOfCPUs = Integer.parseInt(threads);
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(numberOfCPUs));

        }

        System.out.println();
        System.out.println("Quer combinacoes : ");   //0 || 1
        aux = br.readLine();
        WANNACOUNT = aux.equalsIgnoreCase("1") ? true : false;



        start = System.nanoTime();

        questaoDois(palavra,maxLetras,outValues);

        difference = System.nanoTime() - start;
        speedUp = difference.doubleValue();
        writeInFile((String)outValues.get("result"),difference,1,0D);
        System.out.println("Combinacoes realizados : "+ (outValues.get("combos")));
        System.out.println(BigDecimal.valueOf(difference / 1E9) + " Segundos");
        if (outValues.get("result") == null)
            System.out.println("Não encontrada");
        else {
            System.out.println("A palavra chave e : " + outValues.get("result"));
        }

        start = System.nanoTime();

        questaoTres(palavra,maxLetras,outValues);

        difference = System.nanoTime() - start;
        speedUp /= difference.doubleValue();
        speedUp = (((numberOfCPUs /speedUp)-1) / (numberOfCPUs-1))*100;
        System.out.println("Frequencia de codigo sequencial : "+ speedUp);
        writeInFile((String)outValues.get("result"),difference,Integer.parseInt(threads)>0 ? Integer.parseInt(System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism")) : numberOfCPUs ,speedUp);
        System.out.println("Combinacoes realizados : "+ (outValues.get("combos")));
        System.out.println(BigDecimal.valueOf(difference /1E9) + " Segundos");
        if (outValues.get("result") == null)
            System.out.println("Não encontrada");
        else {
            System.out.println("A palavra chave e : " + outValues.get("result"));
        }

        questaoQuatro((String)outValues.get("result"));
    }

    private static void questaoUm()
    {
        System.out.println(stringAsHex(DigestUtils.sha512(getBytesFast("zoom"))));
        System.out.println(stringAsHex(DigestUtils.sha512(getBytesFast("zigzag"))));
        System.out.println(stringAsHex(DigestUtils.sha512(getBytesFast("zirconium"))));
    }

    private static void questaoDois(String palavra,int maxLetras, Map<String,Object> outValues)
    {
        byte[] palavraEncriptada = hexStringToByteArray(palavra);
        List<Byte> result = null;
        int i;
        long combos=0;

            i = 1;
            while (result == null && i <= maxLetras) {

                result =
                        Generator.permutation(
                                (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
                                (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
                                (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
                                (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z')
                                .withRepetitions(i)
                                .stream()
                                .sequential()
                                .filter(item -> Arrays.equals(DigestUtils.sha512(Bytes.toArray(item)), palavraEncriptada))
                                .findAny()
                                .orElse(null);
                if (WANNACOUNT) {
                    if (result == null)
                        combos += Math.pow(26, i);
                    else {
                        long iteration = 0;
                        double max = Math.pow(26, i);
                        boolean find = false;
                        int index;

                        while (max > iteration) {
                            index =0;
                            Iterator<List<Byte>> itr = Generator.permutation(
                                    (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
                                    (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
                                    (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
                                    (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z')
                                    .withRepetitions(i)
                                    .stream()
                                    .skip(iteration)
                                    .limit(MAXCOMBOSPERITERATION)
                                    .sequential()
                                    .collect(Collectors.toList())
                                    .iterator();

                            while(itr.hasNext()) {
                                List<Byte> element = itr.next();
                                index++;
                                if (Arrays.equals(Bytes.toArray(element),Bytes.toArray(result)))
                                {
                                    find = true;
                                    break;
                                }
                            }

                            combos += index;
                            if (find)
                                break;
                            iteration += MAXCOMBOSPERITERATION;
                        }
                    }
                }
                i++;
            }

        outValues.put("combos",combos);
            if (result != null)
                outValues.put("result",new String( Bytes.toArray(result)));

    }

    private static void questaoTres(String palavra, int maxLetras, Map<String,Object> outValues)
    {
        byte[] palavraEncriptada = hexStringToByteArray(palavra);

        //ForkJoinPool pool = new ForkJoinPool();
        List<Byte> result = null;
        int i = 1;
        long combos=0;
        while (result == null && i <= maxLetras) {
            result =
                    Generator.permutation(
                            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
                            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
                            (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
                            (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z')
                            .withRepetitions(i)
                            .stream()
                            .parallel()
                            .filter(item -> Arrays.equals(DigestUtils.sha512(Bytes.toArray(item)), palavraEncriptada))
                            .findAny()
                            .orElse(null);

            if (WANNACOUNT) {
                if (result == null)
                    combos += Math.pow(26, i);
                else {
                    long iteration = 0; // Para 4 caracteres max : 475254
                    double max = Math.pow(26, i);
                    int index;
                    boolean find = false;

                    while (max > iteration) {
                        index =0;
                        Iterator<List<Byte>> itr = Generator.permutation(
                                (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
                                (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
                                (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
                                (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z')
                                .withRepetitions(i)
                                .stream()
                                .skip(iteration)
                                .limit(MAXCOMBOSPERITERATION)
                                .parallel()
                                .collect(Collectors.toList())
                                .iterator();

                        while(itr.hasNext()) {
                            List<Byte> element = itr.next();
                            index++;
                            if (Arrays.equals(Bytes.toArray(element),Bytes.toArray(result)))
                            {
                                find = true;
                                break;
                            }
                        }

                        combos += index;
                        if (find)
                            break;
                        iteration += MAXCOMBOSPERITERATION;
                    }
                }
            }
            i++;
        }
        outValues.put("combos",combos);
        if (result != null)
            outValues.put("result",new String( Bytes.toArray(result)));
    }

    private static void questaoQuatro(String palavra)
    {
        SwingUtilities.invokeLater(() -> {
            BarChart ex = new BarChart(palavra,filename,false);
            ex.setVisible(true);
            BarChart e = new BarChart(palavra,filename,true);
            e.setVisible(true);
        });
    }

    private static final String stringAsHex(byte[] hash) {

        ap.setLength(0);
        for (byte b : hash) {
            ap.append(Character.forDigit((b >>> 4) & 0xF,16));
            ap.append(Character.forDigit(b & 0xF,16));
        }

        return ap.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static byte[] getBytesFast(String str) {
        final int length = str.length();
        final char buffer[] = new char[length];
        str.getChars(0,length,buffer,0);
        final byte b[] = new byte[length];
        for (int j = 0; j < length; j++)
            b[j] = (byte) buffer[j];
        return b;
    }

    private static final String bytesToString(List<Byte> input)
    {
        ap.setLength(0);
        input.forEach(item->ap.append((char)item.byteValue()));
        return ap.toString();
    }

    private static final void writeInFile(String palavra,Long nanoTime, Integer numProcessors,Double speedUp) throws IOException {

        StringBuilder sb = new StringBuilder();
        //sb.append("WORD").append(",").append("TIME").append(",").append("PROCESSORS").append(",").append("ALPHA").append("\n");
        sb.append(palavra).append(",").append(nanoTime).append(",").append(numProcessors).append(",").append(speedUp).append("\n");

        Files.write(Paths.get(filename), sb.toString().getBytes(), StandardOpenOption.APPEND);
    }

}