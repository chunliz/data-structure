public class NBody {
	public static double readRadius(String filename){
		In in=new In(filename);
		int N=in.readInt();
		double r_univ=in.readDouble();
		return r_univ;
	}
	public static Planet[] readPlanets(String filename){
		In in=new In(filename);
		int N=in.readInt();
		double r_univ=in.readDouble();

		Planet[] ps=new Planet[N];
		for(int i=0;i<N;i++){
			Planet p=new Planet(in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readString());
			ps[i]=p;
		}
		return ps;		
	}
	public static void main(String[] args){
		double T=Double.parseDouble(args[0]);
		double dt=Double.parseDouble(args[1]);
		String filename=args[2];

		double r_univ=readRadius(filename);
		Planet[] ps=readPlanets(filename);

		String imageToDraw="images/starfield.jpg";
		StdDraw.setScale(-r_univ,r_univ);
		StdDraw.clear();
		StdDraw.picture(0,0,imageToDraw);
		for(Planet p:ps) p.draw();
		StdDraw.show();
		StdAudio.play("audio/2001.mid");

		double t=0;
		while(t<=T){
			double[] xForce=new double[ps.length];
			double[] yForce=new double[ps.length];
			for(int i=0;i<ps.length;i++){
				xForce[i]=ps[i].calcNetForceExertedByX(ps);
				yForce[i]=ps[i].calcNetForceExertedByY(ps);
			}

			for(int i=0;i<ps.length;i++){
				ps[i].update(dt, xForce[i], yForce[i]);
			}

			StdDraw.picture(0,0,imageToDraw);
			for(Planet p:ps) p.draw();
			StdDraw.show(10);

			t=t+dt;
		}

		StdOut.printf("%d\n", ps.length);
		StdOut.printf("%.2e\n", r_univ);
		for(int i=0;i<ps.length;i++){
			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n", 
				ps[i].xxPos, ps[i].yyPos, ps[i].xxVel, ps[i].yyVel, ps[i].mass, ps[i].imgFileName);
		}
	}
}
