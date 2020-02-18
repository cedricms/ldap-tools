package net.ldap.tools;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LdapToolsApp {

    // An online test server
    private static final String SERVER = "ldap.forumsys.com";
    private static final int PORT = 389;
    private static final String BIND_DN = "cn=read-only-admin,dc=example,dc=com";
    private static final String BIND_PASSWORD = "password";
    private static final String ROOT_SEARCH = "dc=example,dc=com";

    public static void main( String[] args ) {
        LdapToolsApp ldapToolsApp = new LdapToolsApp();
        Hashtable environment = ldapToolsApp.createEnvironment();

        try {
            DirContext context = new InitialDirContext(environment);
            System.out.println("Serveur connection : SUCCESS");

            String userName = "riemann";
            printUserDetails(context, userName);

            printLdapGroups(context);

            context.close();
        } catch (NamingException e) {
            System.out.println("Serveur connection : FAIL");
            e.printStackTrace();
        }
    }

    private static void printLdapGroups(DirContext context) throws NamingException {
        System.out.println("\nGroups :");
        SearchControls ctls = new SearchControls();
        String[] attrIDs = {"ou", "memberOf"};
        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration answer = context.search(ROOT_SEARCH, "(objectclass=*)", ctls);
        while (answer.hasMore()) {
            SearchResult searchResult = (SearchResult) answer.next();
            Attributes attributes = searchResult.getAttributes();

            Attribute ou = attributes.get("ou");
            if (ou != null) {
                System.out.println("ou : " + ou.get(0));
            }
        }
    }

    private static void printUserDetails(DirContext context, String userName) {
        try {
            // Récupération des attributs de l'utilisateur
            Attributes attributes = context.getAttributes("uid=" + userName + ",dc=example,dc=com");

            System.out.println("Recuperation de utilisateur : SUCCES");

            printUserDetails(attributes);
        } catch (NamingException e) {
            System.out.println("Recuperation de dupont : ECHEC");
            e.printStackTrace();
        }
    }

    private static void printUserDetails(Attributes attributes) {
        System.out.println("mail : " + attributes.get("mail"));
        System.out.println("uid : " + attributes.get("uid"));
        System.out.println("name : " + attributes.get("name"));
        System.out.println("objectclass : " + attributes.get("objectclass"));
        System.out.println("department : " + attributes.get("department"));
        System.out.println("sn : " + attributes.get("sn"));
        System.out.println("cn : " + attributes.get("cn"));
    }

    private static Hashtable createEnvironment() {
        Hashtable environment = new Hashtable();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://" + SERVER + ":" + PORT + "/");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, BIND_DN);
        environment.put(Context.SECURITY_CREDENTIALS, BIND_PASSWORD);

        return environment;
    }
}
