import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trab2 {
	public static void main(String[] args) throws Exception 
	{
		
		ArrayList<Endereco> a = new ArrayList<Endereco>();
		RandomAccessFile f = new RandomAccessFile("cep.dat", "r");
		int qtd=8;
		List<RandomAccessFile> lista = new ArrayList<>();
		int k = 0, count=0;
		long qtBlocos = f.length()/300/8;
		long linhasRest = (f.length()-qtBlocos*300*8)/300;
		System.out.println("Linhas que sobraram: "+linhasRest);
		System.out.println("Linhas por bloco: " +qtBlocos);
		for(int i=0;i<qtd;i++) {
			lista.add(new RandomAccessFile("cep"+i+".dat", "rw"));
			count++;
			for(int j=0;j<qtBlocos;j++) {
				Endereco e = new Endereco();
				f.seek(k*300);
				k++;
				e.leEndereco(f);
				a.add(e);
			}
			if(i==qtd-1) {
				if(linhasRest!=0) {
					for(int j=0;j<linhasRest;j++) {
						Endereco e = new Endereco();
						f.seek(k*300);
						k++;
						e.leEndereco(f);
						a.add(e);
					}
				}
			}
			Collections.sort(a,new ComparaCEP());
			for(Endereco e: a)
			{
				e.escreveEndereco(lista.get(i));
			}
			a.clear();
			lista.get(i).close();
		}
		f.close();
		int count2=0,contaArq=0,qtdLista=lista.size();
		count=lista.size();
		lista.clear();
		while(count2<qtd-1) {
			RandomAccessFile f1= new RandomAccessFile("cep"+contaArq+".dat","rw");
			contaArq++;
			RandomAccessFile f2= new RandomAccessFile("cep"+contaArq+".dat","rw");
			contaArq++;
			long tam1=f1.length()/300, tam2 = f2.length()/300;
			System.out.println("Tam 1: "+tam1);
			System.out.println("Tam 2: "+tam2);
			RandomAccessFile exit = new RandomAccessFile("cep"+count+".dat","rw");
			count++;
			k=0;
			int j=0;
			boolean fim1=false, fim2=false, fimBoth=false;
			do {
				if(fim1 && k<tam2) {
					Endereco end2 = new Endereco();
					f2.seek(k*300);
					end2.leEndereco(f2);
					end2.escreveEndereco(exit);
					k++;
					if(k>=tam2) {
						fim2=true;
						if(j>=tam1)
							fimBoth=true;
					}
				}
				else if(j<tam1 && fim2) {
					Endereco end1 = new Endereco();
					f1.seek(j*300);
					end1.leEndereco(f1);
					end1.escreveEndereco(exit);
					j++;
					if(j>=tam1) {
						fim1=true;
						if(k>=tam2)
							fimBoth=true;
					}
				}
				else {
					int intComp=0;
					Endereco end1 = new Endereco();
					f1.seek(j*300);
					end1.leEndereco(f1);
					Endereco end2 = new Endereco();
					f2.seek(k*300);
					end2.leEndereco(f2);
					ComparaCEP comparador = new ComparaCEP();
					intComp = comparador.compare(end1, end2);
					if(intComp<0) {
						end1.escreveEndereco(exit);
						j++;
						if(j>=tam1) {
							fim1=true;
							if(k>=tam2)
								fimBoth=true;
						}
					}
					else {
						end2.escreveEndereco(exit);
						k++;
						if(k>=tam2) {
							fim2=true;
							if(j>=tam1)
								fimBoth=true;
						}
					}
				}
			}while(!fimBoth);
			qtdLista++;
			f1.close();
			f2.close();
			exit.close();
			count2++;
		}
		
		//DELETE
		for(int i=0;i<qtdLista-1;i++) {
			File fdel = new File("cep"+i+".dat");
			if(fdel.delete()){
    			System.out.println(fdel.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete failed.");
    		}
		}
	}
}
