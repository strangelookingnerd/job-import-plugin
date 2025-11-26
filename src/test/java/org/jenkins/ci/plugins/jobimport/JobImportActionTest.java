package org.jenkins.ci.plugins.jobimport;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.List;
import org.jenkins.ci.plugins.jobimport.model.JenkinsSite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Created by evildethow on 29/06/2016.
 */
@WithJenkins
class JobImportActionTest {

  @RegisterExtension
  private static final WireMockExtension wireMock = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort()).build();

  private JobImportClient client;
  private RemoteJenkins remoteJenkins;

  @BeforeEach
  void beforeEach(JenkinsRule rule) throws Exception {
    JobImportGlobalConfig.get().setSites(List.of(new JenkinsSite("Test Site", wireMock.baseUrl())));

    client = new JobImportClient(rule.createWebClient());
    remoteJenkins = new RemoteJenkins(wireMock);
  }

  @ParameterizedTest(name = "recursiveSearch = {0}")
  @ValueSource(booleans = {true, false})
  void doImport(boolean recursiveSearch) throws Exception {
    client.doQuerySubmit(recursiveSearch);

    remoteJenkins.verifyQueried(recursiveSearch);

    client.selectJobs();
    client.doImportSubmit();

    remoteJenkins.verifyImported(recursiveSearch);
  }
}
