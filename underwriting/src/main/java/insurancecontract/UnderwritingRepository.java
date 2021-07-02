package insurancecontract;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="underwritings", path="underwritings")
public interface UnderwritingRepository extends PagingAndSortingRepository<Underwriting, Long>{


}
