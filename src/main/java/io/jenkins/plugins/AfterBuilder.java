package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.User;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : JiaZe Xu
 * @since : 1.0
 */
public class AfterBuilder extends Recorder {

    private final String getOffer;

    @DataBoundConstructor
    public AfterBuilder(String getOffer,String TestCycle,String fillMode) {
        this.getOffer = getOffer;
    }

    public String getCurrentUser(){
        return Objects.requireNonNull(User.current()).getDisplayName();
    }


    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public ListBoxModel doFillTestProjectItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("==请选择==","empty");
            items.add("测试项目A","uuid1");
            items.add("测试项目B","uuid2");
            return items;
        }

        public ListBoxModel doFillFillModeItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("仅用例为P时标记(覆盖F)","fillPassOnlyReplaceF");
            items.add("仅用例为P时标记(不覆盖F)","fillPassOnlyReplaceNone");
            items.add("用例为F和P时都标记(F不覆盖P)","fillAll");
            items.add("用例为F和P时都标记(F覆盖P)","fillAll");
            items.add("不回填任何结果","fillNone");
            return items;
        }

        public ListBoxModel doFillTestCycleItems(@QueryParameter String TestProject) {
            ListBoxModel items = new ListBoxModel();
            if ("empty".equals(TestProject)) {
                return items;
            }
            items.add(TestProject,"11");
            return items;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "TODO DISPLAY NAME";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }
    }
}
