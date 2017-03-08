import edu.cmu.edgecache.recog.predictors.MarkovPredictor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.io.*;
import java.util.*;

/**
 * Created by utsav on 3/7/17.
 */
public class TestMarkov
{
    public static void main(String args[]) throws IOException, InterruptedException
    {
        if (args.length == 2)
        {
            String querydir = args[0];
            String all_objects_path = args[1];

            // Get files in dir
            File folder = new File(querydir);
            String[] listOfFiles = folder.list(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.startsWith("id");
                }
            });
            List<String> test_files = new ArrayList<>();
            List<String> train_files = new ArrayList<>();
            int i = 0;
            for (; i < 0.2*listOfFiles.length; i++)
            {
                test_files.add(querydir + File.separator + listOfFiles[i]);
            }
            for (; i < listOfFiles.length; i++)
            {
                train_files.add(querydir + File.separator + listOfFiles[i]);
            }

            // Get all states
            BufferedReader all_objects_file = new BufferedReader(new FileReader(all_objects_path));

            String line = all_objects_file.readLine();
            ArrayList<String> all_objects = new ArrayList<>();
            do
            {
                String[] chunks = line.split(",");
                String img = chunks[1];
                all_objects.add(img);
                line = all_objects_file.readLine();
            } while ((line != null));

            MarkovPredictor<String> predictor = new MarkovPredictor<>(all_objects, 1);

            train(predictor, train_files);
            System.out.println("Top-10 accuracy:" +  StatUtils.mean(test(predictor, test_files)));

        } else
        {
            System.err.println("Incorrect number of args");
            System.exit(1);
        }
    }

    public static void train(MarkovPredictor<String> predictor, List<String> query_paths) throws IOException
    {
        for (String path : query_paths)
        {
            List<String> queries = parseQueries(path);
            for (int i = 0; i < queries.size() - 1; i++)
            {
                predictor.incrementTransition(queries.get(i), queries.get(i + 1));
            }
        }

    }

    public static double[] test(MarkovPredictor<String> predictor,
                                                        List<String> query_paths) throws IOException
    {
        List<Double> matches = new ArrayList<>();

        for (String path : query_paths)
        {
            List<String> queries = parseQueries(path);
            for (int i = 0; i < queries.size() - 1; i++)
            {
                // Get PDF
                Map<String, Double> pdf = predictor.getNextPDF(queries.get(i));
                // Sort PDF in descending
                List<Map.Entry<String, Double>> sorted_pdf = new ArrayList<>(pdf.entrySet());
                Collections.sort(sorted_pdf, new Comparator<Map.Entry<String, Double>>()
                {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
                    {
                        if(o1.getValue() > o2.getValue())
                            return -1;
                        else if(o1.getValue() < o2.getValue())
                            return 1;
                        return 0;
                    }
                });
                // Check top 10
                boolean flag = false;
                for (int j = 0; j < 10; j++)
                {
                    if(sorted_pdf.get(j).getKey().equals(queries.get(i+1)))
                    {
                        flag = true;
                        break;
                    }
                }
                matches.add(flag?1.0:0.0);

//                System.out.println("Actual:" + queries.get(i+1) +" PDF:" + sorted_pdf);
            }
        }

        return ArrayUtils.toPrimitive((matches.toArray(new Double[0])));
    }

    private static List<String> parseQueries(String path) throws IOException
    {
        // Parse queries
        List<String> queries = new ArrayList<>();
        BufferedReader dir = new BufferedReader(new FileReader(path));
        String line = dir.readLine();
        do
        {
            String[] chunks = line.split(",");
            String img = chunks[0];
            String imgpath = chunks[1];
            if(queries.isEmpty())
                queries.add(img);
            else if(!queries.get(queries.size() - 1).equals(img))
                queries.add(img);
            line = dir.readLine();
        } while ((line != null));
        return queries;
    }
}
