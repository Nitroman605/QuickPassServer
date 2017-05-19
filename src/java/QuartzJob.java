import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/**
 *
 * @author Nitro
 */
public class QuartzJob implements Job {
     public void execute(JobExecutionContext context)
                        throws JobExecutionException {
                Functions.cleanSession();
        }
}
