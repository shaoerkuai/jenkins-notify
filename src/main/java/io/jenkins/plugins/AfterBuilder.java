package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.User;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Optional;

/**
 * @author : JiaZe Xu
 * @since : 1.0
 */
public class AfterBuilder extends Recorder {

    private String TypeReport;

    private String TestProject;

    private String TestCycle;

    private String FillMode;
    private boolean SendMail;

    @DataBoundConstructor
    public AfterBuilder(String reportType, String testProject, String testCycle, String fillMode, boolean sendMail) {
        this.TypeReport = reportType;
        this.TestProject = testProject;
        this.TestCycle = testCycle;
        this.FillMode = fillMode;
        this.SendMail = sendMail;
    }

    public String getTypeReport() {
        return TypeReport;
    }

    public String getTestProject() {
        return TestProject;
    }

    public String getTestCycle() {
        return TestCycle;
    }

    public String getFillMode() {
        return FillMode;
    }

    public boolean getSendMail() {
        return SendMail;
    }

    public String isReportCheck(String value) {
        return this.TypeReport.equalsIgnoreCase(value) ? "true" : "";
    }

    public String getCurrentUser() {
        Optional<User> currentUser = Optional.ofNullable(User.current());
        if (currentUser.isPresent()) {
            return currentUser.get().getId();
        } else {
            return "anonymous";
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return super.perform(build, launcher, listener);
    }


    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            load();
        }

        public String getCurrentUser() {
            Optional<User> currentUser = Optional.ofNullable(User.current());
            if (currentUser.isPresent()) {
                return currentUser.get().getId();
            } else {
                return "anonymous";
            }
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public ListBoxModel doFillTestProjectItems() {
            ListBoxModel items = new ListBoxModel();
            items.add(getCurrentUser(),"anonymous");
            items.add("==请选择==", "empty");
            items.add("测试项目A", "uuid1");
            items.add("测试项目B", "uuid2");
            return items;
        }

        public ListBoxModel doFillFillModeItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("仅用例为P时标记(覆盖F)", "fillPassOnlyReplaceF");
            items.add("仅用例为P时标记(不覆盖F)", "fillPassOnlyReplaceNone");
            items.add("用例为F和P时都标记(F不覆盖P)", "fillAll");
            items.add("用例为F和P时都标记(F覆盖P)", "fillAll");
            items.add("不回填任何结果", "fillNone");
            return items;
        }

        public ListBoxModel doFillTestCycleItems(@QueryParameter String testProject) {
            ListBoxModel items = new ListBoxModel();
            if ("empty".equals(testProject)) {
                return items;
            }
            items.add(testProject, "11");
            return items;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "TODO DISPLAY NAME";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            System.out.println(formData);
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }
    }
}
