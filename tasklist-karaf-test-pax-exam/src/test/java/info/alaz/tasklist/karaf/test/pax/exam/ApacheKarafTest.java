/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.alaz.tasklist.karaf.test.pax.exam;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import java.io.File;
import java.util.Collection;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import info.alaz.tasklist.service.Task;
import info.alaz.tasklist.service.TaskService;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
public class ApacheKarafTest
{
	public static final String KARAF_NAME = "Apache Karaf";

	public static final String MVN_KARAF_GROUP_ID = "org.apache.karaf";
	public static final String MVN_KARAF_ARTIFACT_ID = "apache-karaf";
	public static final String MVN_KARAF_VERSION = "4.1.2";

	public static final String MVN_GROUP_ID = "info.alaz.tasklist";
	public static final String MVN_ARTIFACT_ID = "tasklist-karaf-features";

	@Inject
	protected BundleContext bundleContext;

	@Inject
	protected TaskService taskService;

	public File getConfigFile( String path )
	{
		return new File( this.getClass().getResource( path ).getFile() );
	}

	@Configuration
	public Option[] config()
	{
		MavenArtifactUrlReference karafUrl = maven().groupId( MVN_KARAF_GROUP_ID ).artifactId( MVN_KARAF_ARTIFACT_ID )
				.version( MVN_KARAF_VERSION ).type( "tar.gz" );
		MavenArtifactUrlReference tasklistRepo = maven().groupId( MVN_GROUP_ID ).artifactId( MVN_ARTIFACT_ID ).versionAsInProject()
				.type( "xml" );
		return new Option[]{karafDistributionConfiguration().frameworkUrl( karafUrl ).name( KARAF_NAME ).unpackDirectory(
				new File( "target/exam" ) ), keepRuntimeFolder(),
				features( tasklistRepo, "example-tasklist-service-impl", "example-tasklist-ui" ),
				KarafDistributionOption.debugConfiguration( "5005", true )};
	}

	@Test
	public void test1() throws Exception
	{
		Collection<Task> tasks = taskService.getTasks();
		Assert.assertEquals( 2, tasks.size() );
	}

}
