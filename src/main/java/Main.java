import com.google.common.primitives.Bytes;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.paukov.combinatorics3.Generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static StringBuilder ap = new StringBuilder();
    private static int numberOfCPU;
    private static final long MAXCOMBOSPERITERATION = 10000;
    private static final boolean WANNACOUNT = true;

//TODO especificar o numero de threads


    public static void main(String args[]) throws FileNotFoundException {

        //System.setProperty("propname", "hello world");
        int maxLetras;
        String palavra;
        Long start;
        Long difference;
        Map<String,Object> outValues = new HashMap<>();

        maxLetras = "aaaaa".length();
        palavra = "aaaaa";

        //questaoUm():

        /*
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Maximo de letras : ");
        maxLetras=Integer.parseInt(br.readLine());
        System.out.println();
        System.out.print("Palavra chave : ");
        palavra = br.readLine();
        */

        numberOfCPU = Runtime.getRuntime().availableProcessors();

        start = System.nanoTime();

        questaoDois(palavra,maxLetras,outValues);

        difference = System.nanoTime() - start;
        System.out.println("Combinacoes realizados : "+ (outValues.get("combos")));
        System.out.println(Double.valueOf(difference/10e9) + " Segundos");
        if (outValues.get("result") == null)
            System.out.println("Não encontrada");
        else {
            System.out.println("A palavra chave e : " + bytesToString(((List<Byte>)outValues.get("result"))));
        }

        start = System.nanoTime();

        questaoTres(palavra,maxLetras,outValues);

        difference = System.nanoTime() - start;
        System.out.println("Combinacoes realizados : "+ (outValues.get("combos")));
        System.out.println(Double.valueOf(difference/10e9) + " Segundos");
        if (outValues.get("result") == null)
            System.out.println("Não encontrada");
        else {
            System.out.println("A palavra chave e : " + bytesToString(((List<Byte>)outValues.get("result"))));
        }


        questaoQuatro();


        writeInFile();
    }

    private static void questaoUm()
    {
        System.out.println(stringAsHex(DigestUtils.sha512(getBytesFast("zoom"))));
        System.out.println(stringAsHex(DigestUtils.sha512(getBytesFast("zigzag"))));
        System.out.println(stringAsHex(DigestUtils.sha512(getBytesFast("zirconium"))));
    }

    private static void questaoDois(String palavra,int maxLetras, Map<String,Object> outValues)
    {
        byte[] palavraEncriptada = DigestUtils.sha512(palavra);
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
                                .filter(item -> Arrays.compare(DigestUtils.sha512(Bytes.toArray(item)), palavraEncriptada) == 0)
                                .findAny()
                                .orElse(null);
                if (WANNACOUNT) {
                    if (result == null)
                        combos += Math.pow(26, i);
                    else {
                        long iteration = 0;
                        double max = Math.pow(26, i);
                        int index =0;



                        while (max > iteration) {


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
                                if (Arrays.compare(Bytes.toArray(element),Bytes.toArray(result)) == 0)
                                {
                                    index++;
                                    break;
                                }
                            }

                            combos += index;
                            if (index == -1) {
                                iteration += MAXCOMBOSPERITERATION;
                            } else {
                                break;
                            }
                        }
                    }
                }
                i++;
            }

        outValues.put("combos",combos);
        outValues.put("result",result);
    }

    private static void questaoTres(String palavra, int maxLetras, Map<String,Object> outValues)
    {
        byte[] palavraEncriptada = DigestUtils.sha512(palavra);
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
                            //.forEach(x->{x.forEach(y->{System.out.print((char)y.byteValue());});System.out.println();});
                            .filter(item -> Arrays.compare(DigestUtils.sha512(Bytes.toArray(item)), palavraEncriptada) == 0)
                            .findAny()
                            .orElse(null);

            if (WANNACOUNT) {
                if (result == null)
                    combos += Math.pow(26, i);
                else {
                    long iteration = 0;
                    double max = Math.pow(26, i);
                    int index =0;

                    while (max > iteration) {

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
                            if (Arrays.compare(Bytes.toArray(element),Bytes.toArray(result)) == 0)
                            {
                                index++;
                                break;
                            }
                        }

                        combos += index;
                        if (index == -1) {
                            iteration += MAXCOMBOSPERITERATION;
                        } else {
                            break;
                        }
                    }
                }
            }
            i++;
        }
        outValues.put("combos",combos);
        outValues.put("result",result);

    }

    private static void questaoQuatro()
    {
        AmdahlUI aux = new AmdahlUI(numberOfCPU,20);
        GraphPanel graph = new GraphPanel(40,10);
    }

    private static final String stringAsHex(byte[] hash) {

        ap.setLength(0);
        for (byte b : hash) {
            ap.append(Character.forDigit((b >>> 4) & 0xF,16));
            ap.append(Character.forDigit(b & 0xF,16));
        }

        return ap.toString();
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


    private static final void writeInFile() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File("data.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append("WORD").append(",").append("TIME").append(",").append("PROCESSORS").append("\n");



        pw.write(sb.toString());
        pw.close();
    }
}