package systems.opalia.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import systems.opalia.launcher.exception.UncheckedResolutionException;


public final class ArtifactResolver {

    private final List<RemoteRepository> remoteRepositories;
    private final LocalRepository localRepository;
    private final RepositorySystem repositorySystem;

    public ArtifactResolver(List<RemoteRepository> remoteRepositories, LocalRepository localRepository) {

        this.remoteRepositories = new ArrayList<>(remoteRepositories);
        this.localRepository = localRepository;
        this.repositorySystem = newRepositorySystem();
    }

    public ArtifactResolver(LinkedHashMap<String, String> remoteRepositories, String localRepository) {

        this(remoteRepositories.entrySet().stream()
                        .map(x -> new RemoteRepository.Builder(x.getKey(), "default", x.getValue()).build())
                        .collect(Collectors.toList()),
                new LocalRepository(localRepository));
    }

    public Artifact resolve(DefaultArtifact artifact) {

        final var session = newRepositorySystemSession();
        final var artifactRequest = new ArtifactRequest();

        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(remoteRepositories);

        try {

            final var result = repositorySystem.resolveArtifact(session, artifactRequest);

            if (result.isMissing())
                throw new UncheckedResolutionException(new Exception("Cannot resolve artifact " + artifact));

            return result.getArtifact();

        } catch (ArtifactResolutionException e) {

            throw new UncheckedResolutionException(e);
        }
    }

    public List<Artifact> resolveTransitive(DefaultArtifact artifact, String scope) {

        return resolveTransitive(artifact, scope, Collections.emptyList());
    }

    public List<Artifact> resolveTransitive(DefaultArtifact artifact, String scope, List<String> scopeFilter) {

        // scope constants: org.eclipse.aether.util.artifact.JavaScopes

        final var session = newRepositorySystemSession();
        final var collectRequest = new CollectRequest();

        collectRequest.setRoot(new Dependency(artifact, scope));
        collectRequest.setRepositories(remoteRepositories);

        final var dependencyRequest =
                new DependencyRequest(collectRequest, DependencyFilterUtils.classpathFilter(scopeFilter));

        try {

            final var results = repositorySystem.resolveDependencies(session, dependencyRequest).getArtifactResults();

            for (final var artifactResult : results)
                if (artifactResult.isMissing())
                    throw new UncheckedResolutionException(artifactResult.getExceptions().get(0));

            return results.stream().map(ArtifactResult::getArtifact).collect(Collectors.toList());

        } catch (DependencyResolutionException e) {

            throw new UncheckedResolutionException(e);
        }
    }

    private RepositorySystem newRepositorySystem() {

        final var locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }

    private DefaultRepositorySystemSession newRepositorySystemSession() {

        final var session = MavenRepositorySystemUtils.newSession();

        session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, localRepository));

        // if needed: session.setTransferListener(new ConsoleTransferListener());
        // if needed: session.setRepositoryListener(new ConsoleRepositoryListener());

        return session;
    }
}
