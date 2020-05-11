package defo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

///////////////////////////////////////////////////////////////////////////////////
public class KM {
	public void kMeans(){	// FCM algorithm method
		File file = new File("C:\\JavaIO\\Input\\g2-2-30.txt");	// The file name
		int d = 2;	// the number of dimension of the data
		int data1[][] = DataRead(file,d);	// Load the data
		System.out.println();

		///////////////////////////////
		// Paramter Set & Initiation //
		///////////////////////////////
		int c=2;											// the number of clusters
		int n=data1.length;									// the number of elements
		int maxtry=5;										// the number of maximum trials
		int maxite=100;										// the number of maximum iteration
		int rseed=1;										// random seed
		String dist="Euclid";								// the distance metric type
		double F [][] = new double [maxite][maxtry];		// Objective function value
		double Uarc [][] = new double [maxtry][n];		// Archive for membership
		double Xctrarc [][][] = new double [maxtry][c][d];	// Archive for cluster center

		// data normalization
		double data[][] = new double[n][d];
//		data=NormStd.Normalization(data1);
		data=NormStd.Standardization(data1);


		Sfmt rnd = new Sfmt(rseed);
		//
		int U [] = new int [n];		// the cluster belonging vector
		double Xctr [][] = new double [c][d];	// the coordinate of cluster center


		///////////////////////
//******// k-Means Main Loop //********************************************************
		///////////////////////
		for(int trial=0; trial<maxtry; trial++) {
			for(int i=0; i<n; i++) {
				U[i] = rnd.NextInt(c);	// initiation of random number
				//System.out.println(U[i]);
			}

			for(int ite=0; ite<maxite; ite++) {
				Xctr = CalcCtr(U,data,c);					// the cluster center update
				U = MembershipUpdate(data,Xctr,dist);
				double f = CalcObjFunc(U,data,Xctr,dist);	// Objective function update

				F[ite][trial] = f;	// Objective function update
				System.out.printf("Iteration: \t");
				System.out.printf("%d - %d\t",trial+1,ite+1);
				System.out.printf("OF Value: \t");
				System.out.printf("%.3f\n",F[ite][trial]);
			}

			// Data preservation for the archives
			for(int i=0; i<n; i++) {
				Uarc[trial][i]=U[i];
			}
			for(int i=0; i<c; i++) {
				for(int j=0; j<d; j++) {
					Xctrarc[trial][i][j]=Xctr[i][j];
				}
			}

		}

//*********************************************************************************

		// Extraction of the best data

		double Flast [] = new double [maxtry];
		for(int i=0; i<maxtry; i++) {
			Flast[i] = F[maxite-1][i];
		}
		double Fmin=9999999;
		int Find=0;
		for(int i=0; i<maxtry; i++) {
			if(Flast[i] < Fmin) {
				Find=i;
				Fmin=Flast[i];
			}
		}

		// Data output
		String Ufile = "C:\\JavaIO\\Output\\kM\\U.txt";
		DataWriteVec(Ufile,Uarc[Find]);
		String Xfile = "C:\\JavaIO\\Output\\kM\\X.txt";
		DataWrite(Xfile,data);
		String Xctrfile = "C:\\JavaIO\\Output\\kM\\Xctr.txt";
		DataWrite(Xctrfile,Xctrarc[Find]);

	}

///////////////////////////////////////////////////////////////////////////////////

		/////////////
//******// Methods //**************************************************************
		/////////////
	//public int[][] DataRead(int n){
	public static int[][] DataRead(final File file, int d) {
        List<ArrayList<Integer>> lists = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < d; i++) {
            lists.add(new ArrayList<Integer>());
        }
        BufferedReader br = null;
        try {
            // Read the file, save data to List<Integer>
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                // Add nth integer to lists[n]
                List<Integer> ints = parse_line(line,d);
                for (int i = 0; i < d; i++) {
                    (lists.get(i)).add(ints.get(i));
                }
            }

            // convert lists to 2 Integer[]
            Integer[] array1 = lists.get(0).toArray(new Integer[lists.size()]);
            int n=array1.length;
            int data[][] = new int[n][d];

            int j=0;
            while(j<d) {
            	// convert lists to 2 Integer[]
                Integer[] array = lists.get(j).toArray(new Integer[lists.size()]);
	            for(int i=0; i<n; i++) {
	                	data[i][j]=array[i];
	            }

	            j++;
            }

            return data;

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                // ignore error
            }
        }
        return null;
    }

    // parse 2 integers as a line of String
    private static List<Integer> parse_line(String line, int d) throws Exception {
        List<Integer> ans = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() != d) {
            throw new Exception("Bad line: [" + line + "]");
        }
        while (st.hasMoreElements()) {
            String s = st.nextToken();
            try {
                ans.add(Integer.parseInt(s));
            } catch (Exception ex) {
                throw new Exception("Bad Integer in " + "[" + line + "]. " + ex.getMessage());
            }
        }
        return ans;
    }

    private static void DataWrite(String file_name, double data[][]) {
    	int n = data.length;	// the number of rows
    	int m = data[0].length;	// the number of colmuns

    	try {
            PrintWriter pw = new PrintWriter(file_name);
            for(int i=0; i<n; i++) {
            	for(int j=0; j<m; j++) {
            		if(j<m-1) {
                		pw.format("%.3f\t", data[i][j]);
            		}else {
                		pw.format("%.3f\n", data[i][j]);
            		}
                }
            }

            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void DataWriteVec(String file_name, double data[]) {
    	int n = data.length;	// the number of rows

    	try {
            PrintWriter pw = new PrintWriter(file_name);
            for(int i=0; i<n; i++) {
        		pw.format("%.3f\n", data[i]);
            }
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // cluster centroid calculation method
    private static double [][] CalcCtr(int U[], double X[][], int c){
    	int n = X.length;		// the number of elements
       	int d = X[0].length;	// the number of dementions
    	double ctr [][] = new double [c][d];	// the cluster center
    	StatCalcMat mat = new StatCalcMat();

    	for(int i=0; i<d; i++) {	    // loop for dimension
    		for(int j=0; j<c; j++) {	// loop for cluster
        		List<Double> list = new ArrayList<Double>();
        		for(int k=0; k<n; k++) {	// loop for element
        			if(U[k]==j) {
        				list.add(X[k][i]);

        			}
        		}
        		Double [] array = list.toArray(new Double[list.size()]);
        		double [] meanvec = new double [array.length];
        		for(int k=0; k<array.length; k++) {
        			meanvec[k] = array[k];
        		}
        		ctr[j][i] = mat.MeanVecDouble(meanvec);
    		}
    	}
    	return ctr;
    }

    private static int [] MembershipUpdate(double X[][], double ctr[][], String dist) {
    	int n = X.length;		// the number of elements
    	int c = ctr.length;		// the cluster center
    	int [] U = new int [n];	// new cluster membership
    	double a;

    	for(int i=0; i<n; i++) {
    		double amin = 99999999;
    		int ind = 0;
        	for(int j=0; j<c; j++) {
        		a = Dist2Points(X[i], ctr[j], dist);
        		if(a < amin) {
        			amin = a;
        			ind = j;
        		}
        	}
        	U[i] = ind;
    	}

    	return U;
    }

    private static double CalcObjFunc(int U[], double X[][], double ctr[][], String dist) {
    	double F=0;
		int n=X.length;			// the number of elements
		int c=ctr.length;		// the number of clusters

		for(int i = 0; i<n; i++) {
			for(int j = 0; j<c; j++) {
				F = F + Dist2Points(X[i],ctr[j],dist);
			}
		}

    	return F;
    }

    private static double Dist2Points(double X[], double Y[], String dist) {
    	int d = X.length;
    	double D = 0;
//		int count=1;
		double xsum=0;

		switch (dist) {
		case "Euclid":	// Metric: Euclidean distance

			for(int i=0; i<d; i++) {
				xsum = xsum + Math.pow((X[i]-Y[i]),2);
			}
			D=Math.sqrt(xsum);

			break;
			/*
		case "SEuclid":	// Metric: Scaled Euclidean distance
			double[] X = new double[nd];
			double[] sigma = new double[nf];
			double val;

			StatCalc stat = new StatCalc();
			for(int i=0; i<nf; i++) {
				X = getCul(data,i);
				sigma[i] = stat.Var(X);
			}

			for(int i=0; i<nd; i++) {
				for(int j=count; j<nd; j++) {
					for(int k=0; k<nf; k++) {
						val = ((Math.pow((data[i][k]-data[j][k]),2)) / sigma[k]);
						if(Double.isNaN(val) == false) {
							xsum = xsum + val;
						}
					}
					D[i][j]=Math.sqrt(xsum);
					xsum = 0;
				}
				count = count+1;
			}
			D = Dissim.matUcopy2L(D);

			break;
		case "City":	// Metric: city block distance

			for(int i=0; i<nd; i++) {
				for(int j=count; j<nd; j++) {
					for(int k=0; k<nf; k++) {
						xsum = xsum + Math.abs(data[i][k]-data[j][k]);
					}
					D[i][j] = xsum;
					xsum = 0;
				}
				count = count+1;
			}
			D = Dissim.matUcopy2L(D);

			break;
		case "Chebyshev":	// Metric: Chebyshev distance
			double dmax = 0;
			double a = 0;

			for(int i=0; i<nd; i++) {
				for(int j=count; j<nd; j++) {
					for(int k=0; k<nf; k++) {
						a = Math.abs(data[i][k]-data[j][k]);
						if(dmax < a) {
							dmax = a;
						}
					}
					D[i][j] = dmax;
					dmax=0;
				}
				count=count+1;
			}
			D = Dissim.matUcopy2L(D);

			break;
		case "Mahalanobis":
			break;
		case "Cosine":
			break;
			*/
		}
		return D;
	}

//*********************************************************************************


}
