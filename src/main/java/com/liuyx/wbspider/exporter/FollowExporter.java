package com.liuyx.wbspider.exporter;

import au.com.bytecode.opencsv.CSVWriter;
import com.liuyx.wbspider.model.Follower;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

public class FollowExporter {

    public static void exportSelfFollowersToCSV(List<Follower> myFollowers, String savePath){
        String filename = savePath + "myfollowers_" + new Date().getTime() + ".csv";
        CSVWriter writer = null;
        try{
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }

            writer = new CSVWriter(
                    new OutputStreamWriter(new FileOutputStream(file),"GBK"),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER
            );
            String[] header = {"uid", "nickname", "introduction", "from", "href"};
            writer.writeNext(header);
            for(Follower follower : myFollowers){
                writer.writeNext(new String[]{
                        follower.getUid(),
                        follower.getNickname(),
                        follower.getIntroduction(),
                        follower.getFrom(),
                        follower.getHref()
                });
            }
            System.out.println("Export to: " + filename + " succeed");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
