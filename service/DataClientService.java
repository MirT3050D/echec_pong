
package service;

import com.echec_pong.service.GameConfigServiceRemote;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.ArrayList;

public class DataClientService {

    private GameConfigServiceRemote configEJB;

    public DataClientService() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        jndiProps.put(Context.SECURITY_PRINCIPAL, "Mir");
        jndiProps.put(Context.SECURITY_CREDENTIALS, "admin123");
        jndiProps.put("jboss.naming.client.ejb.context", true);
        jndiProps.put("org.jboss.ejb.client.scoped.context", true);

        Context context = new InitialContext(jndiProps);
        String jndiName = "ejb:/echec_pong-1.0-SNAPSHOT/GameConfigService!com.echec_pong.service.GameConfigServiceRemote";
        configEJB = (GameConfigServiceRemote) context.lookup(jndiName);
    }

    // Appelle le service distant pour sauvegarder la configuration
    public void saveConfig(ArrayList<Integer> vies) throws Exception {
        configEJB.saveConfig(vies);
    }

    // Appelle le service distant pour récupérer la configuration
    public ArrayList<Integer> getConfig() throws Exception {
        return configEJB.getConfig();
    }
}