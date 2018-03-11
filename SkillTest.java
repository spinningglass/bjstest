/*
 * Brice Smith
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 *  Search through an organization on github.com for users that don't have a name entered.
 *  If found then email the user asking them to enter their name.
 *  Keep a list of users who are emailed or who sending an email failed.
 * 
 */
public class SkillTest {

    public static void main(String[] args) {
        // variables
        SkillTest st = new SkillTest();
        String orgName = ""; // the name of the organization you wish to search
        String ownerUser = ""; // login for an owner within the organization
        String ownerPersonalKey = ""; // personal access token generated for the owner listed above

        String credentials = ownerUser + ":" + ownerPersonalKey;
        
        ArrayList<String> emailedUsers = new ArrayList<>();
        ArrayList<String> emailUserFailure = new ArrayList<>();

        try {
            // get org users
            String urlText = "https://api.github.com/orgs/" + orgName + "/members";
            String usersString = st.gitHubCall(urlText, credentials);
            ObjectMapper om = new ObjectMapper();
            List<Map<String,Object>> usersList = om.readValue(usersString, new TypeReference<List<Map<String,Object>>>(){});
            
            ArrayList<String> userNames = new ArrayList();
            for (Map<String, Object> thisDude : usersList) {
                if (thisDude.containsKey("login")) {
                    userNames.add(thisDude.get("login").toString());
                }
            }
            
            // get user properties
            for (String userName : userNames) {
                urlText = "https://api.github.com/users/" + userName;
                String thisUser = st.gitHubCall(urlText, credentials);
                Map<String, Object> userMap = om.readValue(thisUser, new TypeReference<Map<String,Object>>(){});
                
                String thisUsersName = null;
                String thisUsersEmail = null;
                if (userMap.containsKey("name")) {
                    Object nameObj = userMap.get("name");
                    if (nameObj != null) {
                        thisUsersName = userMap.get("name").toString();
                    }
                }
                if (userMap.containsKey("email")) {
                    Object emailObj = userMap.get("email");
                    if (emailObj != null) {
                        thisUsersEmail = userMap.get("email").toString();
                    }
                }
                
                //System.out.println(userName + " " + thisUsersName + " " + thisUsersEmail);
                
                // if thisUserName is null then email the user if thisUserEmail isn't null
                if (thisUsersName == null || thisUsersName.equals("")) {
                    if (thisUsersEmail == null || thisUsersEmail.equals("")) {
                        // unable to send email
                        // if the user doesn't have a public email on github or the authentication doesn't have sufficient privledge
                        emailUserFailure.add(userName);
                    }
                    else {
                        try {
                            st.emailUser(thisUsersEmail);
                            emailedUsers.add(userName + "(" + thisUsersEmail + ")");
                        }
                        catch (Exception e) {
                            //e.printStackTrace();
                            System.out.println("Unable to email user " + userName + " with email '" + thisUsersEmail + "'");
                            emailUserFailure.add(userName);
                        }
                    }
                }
            }

            // send to aws
            if (!emailedUsers.isEmpty() || !emailUserFailure.isEmpty()) {
                st.sendFilesToAws(emailedUsers, emailUserFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String gitHubCall (String urlText, String credentials) throws Exception {
        URL url = new URL(urlText);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        String basicAuth = "Basic " + new String(Base64.getEncoder().encodeToString(credentials.getBytes()));
        conn.setRequestProperty("Authorization", basicAuth);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String response = "";
        String output;
        while ((output = br.readLine()) != null) {
            response += output;
        }

        conn.disconnect();

        return response;
    }

    private void emailUser(String email) throws Exception {
        throw new Exception ("Emailing users not yet completed");
    }
    
    public void sendFilesToAws (ArrayList<String> emailedUsers, ArrayList<String> emailUserFailures) throws Exception {
        for (String emailedUser : emailedUsers) {
            System.out.println("EMAILED: " + emailedUser);
        }
        
        for (String emailUserFailure : emailUserFailures) {
            System.out.println("NOT EMAILED: " + emailUserFailure);
        }
    }
}
