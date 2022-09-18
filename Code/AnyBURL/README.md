#Explaining Predictions

In our latest publication we applied AnyBURL to explain predictions. These predictions might have been made by AnyBURL or any other knowledge graph completion technique. The input required is a set of triples that you want to explain listed in a file called target.txt. Suppose for example that a model predicted 01062739 as tail of the query 00789448 _verb_group ? (example taken from WN18RR. Than you add 00789448 _verb_group 01062739 to your target.txt file.

The folder of the target.txt file is the first argument. The explanation and temporary results (e.g., the rule file) are stored in that folder. The folder to the dataset is the second argument. It is assumed that the relevant files in that folder are the files train.txt, valid.txt, and test.txt. You call the explanation code as follows:

java -Xmx3G -cp AnyBURL-22.jar de.unima.ki.anyburl.Explain explanations/ data/WN18RR/
Several output files are generated. For details we refer to our IJCAI 2022 publication listed below. If you are mainly interested in an explanation look at the file delete-verbose.txt. With respect to the example mentioned above, you will find such an entry:

00789448 _verb_group 01062739 01062739 _verb_group 00789448 572 533 0.9318181818181818 _verb_group(X,Y) <= _verb_group(Y,X)
This means: The strongest reason for deriving the triple at the beginning is the second triple (which can be found in the training set) together with the rule listed at the end. Here we can observe that the symmetry of _verb_group was (probably) the reason for the prediction. Again, more details can be found in our publication.

The code is currently restricted to cyclic rules of length 2 and acyclic rules of length 1. If no rule can be found within that language bias, our approach puts a null at the end of the line. This setting covered most of the testcases of the dataset we used in our experiments (we used the same datasets that has been used in another publication to compare against). Recently we detected that this restriction does, unfortunately, not work well for predictions for which there are no clear signals in the dataset.

More on: https://web.informatik.uni-mannheim.de/AnyBURL/ (under extentions)
