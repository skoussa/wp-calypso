/**
 * External Dependencies
 */
const path = require( 'path' );

const isDevelopment = process.env.NODE_ENV === 'development';

module.exports = {
	context: path.resolve( __dirname, 'app' ),
	entry: './index.js',
	output: {
		path: path.join( __dirname, 'build' ),
		filename: 'index.js',
		libraryTarget: 'commonjs2',
	},
	watch: isDevelopment,
	watchOptions: {
		ignored: /node_modules/,
	},
	target: 'electron-main',
	devtool: 'inline-cheap-source-map',
	node: {
		__filename: true,
		__dirname: false,
	},
	resolve: {
		extensions: [ '.json', '.js', '.jsx', '.ts', '.tsx' ],
		modules: [ 'node_modules' ],
	},
	externals: [ 'keytar', 'superagent', 'ws' ],
	mode: isDevelopment ? 'development' : 'production',
};
