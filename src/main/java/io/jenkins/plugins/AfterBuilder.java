package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : JiaZe Xu
 * @since : 1.0
 */
public class AfterBuilder extends Recorder {

    private static final Logger log = LoggerFactory.getLogger(AfterBuilder.class);
    private final String TypeReport;

    private final String TestProject;

    private final String TestCycle;

    private final String FillMode;

    private final String DefaultRecorder;

    private final boolean SendMail;

    private final boolean SendDefaultMail;

    @DataBoundConstructor
    public AfterBuilder(String reportType,
                        String testProject,
                        String testCycle,
                        String fillMode,
                        boolean sendMail,
                        boolean sendDefaultMail,
                        String defaultRecorder) {
        this.TypeReport = reportType;
        this.TestProject = testProject;
        this.TestCycle = testCycle;
        this.FillMode = fillMode;
        this.SendMail = sendMail;
        this.DefaultRecorder = defaultRecorder;
        this.SendDefaultMail = sendDefaultMail;
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

    public String getDefaultRecorder() {
        return DefaultRecorder;
    }

    public boolean getSendDefaultMail() {
        return SendDefaultMail;
    }

    public String isReportCheck(String value) {
        if (Objects.isNull(this.TypeReport)) {
            return "";
        }
        return this.TypeReport.equalsIgnoreCase(value) ? "true" : "";
    }

    public String getCurrentUser() {
        var currentUser = Optional.ofNullable(User.current());
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
        var logger = listener.getLogger();
        var causes = build.getCauses();
        var build_number = build.getEnvironment(listener).get("BUILD_NUMBER");
        var workspacePath = Objects.requireNonNull(build.getWorkspace()).toURI();
        // 获取build的causes
        var usernameBuildCause = "unknown";
        for (Cause cause : causes) {
            if (cause instanceof Cause.UserIdCause) {
                // 当用户发起时，记录发起人
                var userIdCause = (Cause.UserIdCause) cause;
                usernameBuildCause = userIdCause.getUserName();
                break;
            }
        }
        // 以该人员身份回填
        logger.println("******Start upload test report result to cloud******");
        logger.println("Workspace Path: " + workspacePath.getPath());
        logger.println("Build Number:" + build_number);
        logger.println("Build Cause: " + usernameBuildCause);
        logger.println("Build Status: " + build.getResult());
        logger.printf("Test Project Id: %s%n", this.TestProject);
        logger.printf("Test Cycle Id: %s%n", this.TestCycle);
        logger.printf("Test Project: %s%n", this.TestProject);
        logger.printf("Send Email?: %s%n", this.SendMail);
        return true;
    }


    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        @JavaScriptMethod
        public LinkedHashMap<String, String> fetchProjectId() {
            LinkedHashMap<String, String> projectObj = new LinkedHashMap<>();
            projectObj.put(getCurrentUser(), "nihao");
            projectObj.put("js请选择", "empty");
            projectObj.put("js测试项目A", "uuid1");
            projectObj.put("js测试项目B", "uuid2");
            projectObj.put("js测试项目C", "uuid3");
            return projectObj;
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
            // TODO 从接口根据currentUser添加项目
            // GET /open/jenkins/testProjects?userId=
            return new ListBoxModel();
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
            // TODO 从接口根据currentUser和PorjectItem获取项目
            // GET /open/jenkins/testCycles?userId=**&projectId=**
//            items.add(testProject+"11", "11");
            items.add(testProject + "——22", "22");
            items.add(testProject + "——33", "33");
            return items;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "DEMO PLUGIN";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }
    }
}
